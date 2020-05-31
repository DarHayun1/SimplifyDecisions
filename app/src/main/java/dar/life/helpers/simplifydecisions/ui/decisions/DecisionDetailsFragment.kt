package dar.life.helpers.simplifydecisions.ui.decisions

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.transition.TransitionInflater
import android.view.*
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.amlcurran.showcaseview.ShowcaseView
import com.github.amlcurran.showcaseview.targets.ViewTarget
import com.google.android.material.textfield.TextInputLayout
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.data.Decision
import dar.life.helpers.simplifydecisions.data.Goal
import dar.life.helpers.simplifydecisions.data.ReminderObj
import dar.life.helpers.simplifydecisions.databinding.DecisionDetailsFragmentBinding
import dar.life.helpers.simplifydecisions.reminders.AlarmScheduler
import dar.life.helpers.simplifydecisions.ui.Instruction
import dar.life.helpers.simplifydecisions.ui.UiUtils
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter.ofLocalizedDate
import java.time.format.DateTimeFormatter.ofLocalizedDateTime
import java.time.format.FormatStyle
import java.util.*

class DecisionDetailsFragment : Fragment(), OnGoalClickListener {

    private lateinit var mShowcaseView: ShowcaseView
    private var mFirstTime: Boolean = false
    private var mDialogView: View? = null
    private lateinit var mContext: Context

    private var mDecision: Decision?
        get() = viewModel.lastUsedDecision
        set(value) {
            viewModel.lastUsedDecision = value
        }

    private val mBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backPressed()
            }
        }

    private var _binding: DecisionDetailsFragmentBinding? = null
    private val binding get() = _binding!!

    companion object {
        val REMINDER_ID: String = "reminder_id_extra"

        fun newInstance() = DecisionDetailsFragment()
    }

    private lateinit var viewModel: DecisionsViewModel

    private val args: DecisionDetailsFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null
            && args.isNew
        )
            mFirstTime = true
        sharedElementEnterTransition =
            TransitionInflater.from(mContext).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DecisionDetailsFragmentBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.decision_details_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)
            .get(DecisionsViewModel::class.java)
        requireActivity().onBackPressedDispatcher
            .addCallback(mBackPressedCallback)
        initToolbar()
        initViews()
        if (mFirstTime)
            editDecisionTitle(true)
    }

    private fun initToolbar() {
        binding.decisionDetailsToolbar.title = ""
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.decisionDetailsToolbar)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.decisionDetailsToolbarTitle.transitionName = args.decisionId.toString()
        binding.decisionDetailsToolbarTitle.text = args.decisionTitle
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit_decision_title -> {
                editDecisionTitle()
                true
            }
            R.id.action_collaborate_decision -> {
                handleCollaborateClick()
                true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }

    private fun initViews() {
        viewModel.getDecisionById(args.decisionId)
            .observe(viewLifecycleOwner, Observer {
                it?.let {
                    mDecision = it
                    populateUi(it)
                }
            })
        setupGoals()
    }

    private fun setupGoals() {
        val layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        binding.goalsRv.adapter = GoalsAdapter(mContext, this)
        binding.goalsRv.layoutManager = layoutManager

        binding.addAGoal.setOnClickListener { onNewGoalRequest() }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleCollaborateClick() {
        Toast.makeText(mContext, "Upcoming feature (: ", Toast.LENGTH_SHORT).show()
    }

    private fun editDecisionTitle(isNew: Boolean = false) {
        val dialogBuilder: AlertDialog = AlertDialog.Builder(mContext).create()
        val dialogView = layoutInflater.inflate(R.layout.edit_title_layout, null)

        val textInputLayout: TextInputLayout = dialogView.findViewById(R.id.text_input_layout)
        val cancelBtn: Button = dialogView.findViewById(R.id.et_cancel_button)
        val saveBtn: Button = dialogView.findViewById(R.id.et_save_button)

        textInputLayout.hint = getString(R.string.decision_title_hint)
        textInputLayout.editText?.setText(mDecision?.title)
        cancelBtn.setOnClickListener {
            dialogBuilder.dismiss()
        }
        saveBtn.setOnClickListener {
            if (textInputLayout.editText?.length()!! <= textInputLayout.counterMaxLength) {
                mDecision?.title = textInputLayout.editText?.text.toString()
                binding.decisionDetailsToolbarTitle.text = mDecision?.title
                dialogBuilder.dismiss()
                if (isNew) startHelpMode()
            } else
                Toast.makeText(
                    mContext,
                    "Title length is limited to max ${textInputLayout.counterMaxLength}" +
                            " characters", Toast.LENGTH_SHORT
                ).show()

        }
        dialogBuilder.setView(dialogView)
        dialogBuilder.show()
        if (isNew) textInputLayout.editText?.selectAll()
    }

    private fun startHelpMode() {
        val instructions = getInstruction()
        mShowcaseView = ShowcaseView.Builder(activity)
            .withHoloShowcase()
            .hideOnTouchOutside()
            .setStyle(R.style.ShowcaseTheme)
            .setTarget(ViewTarget(instructions.view))
            .setContentTitle(instructions.title)
            .setContentText(instructions.text.trimMargin())
            .build()
        mShowcaseView.hideButton()
    }

    private fun isHelpMode() = this::mShowcaseView.isInitialized && mShowcaseView.isShown

    private fun hideHelpIfShown(): Boolean {
        if (isHelpMode()) {
            mShowcaseView.hide()
            return true
        }
        return false
    }

    private fun getInstruction(): Instruction = Instruction(
        getString(R.string.first_goal_title),
        getString(R.string.first_goal_text),
        binding.addAGoal
    )

    private fun backPressed() {
        if (!hideHelpIfShown()) {
            clearCallback()
            findNavController().popBackStack()
        }
    }

    private fun clearCallback() {
        mBackPressedCallback.remove()
    }

    private fun populateUi(decision: Decision) {

        binding.decisionDateTv.text = decision.date.format(ofLocalizedDate(FormatStyle.LONG))

        binding.decisionDetailsToolbarTitle.text = decision.title

        //If the user navigated from a reminder it will expand, otherwise, the first
        val remindersGoal = decision.goals.find { it.expanded = false
            it.reminder.id == args.reminderId}
            if (remindersGoal != null){
                remindersGoal.expanded = true
            }else{
                expandNextGoal(decision.goals)
            }
        (binding.goalsRv.adapter as GoalsAdapter).goalsList = decision.goals


    }

    private fun expandNextGoal(goals: MutableList<Goal>) {
        goals.forEach{
            it.expanded = false
        }
        goals.sortBy {
            it.epochDueDate ?:
            LocalDate.now().plusYears(5).toEpochDay()}
        goals.firstOrNull { goal ->
            goal.epochDueDate!=null &&
                LocalDate.ofEpochDay(goal.epochDueDate!!).isAfter(LocalDate.now()) }
            ?.let { it.expanded = true }
    }

    override fun onNewGoalRequest() {
        hideHelpIfShown()
        openEditGoalDialog()
    }

    private fun openEditGoalDialog(goalPos: Int = -1) {
        val dialogBuilder: AlertDialog = AlertDialog.Builder(mContext).create()
        var goal = Goal("New Goal")
        val dialogView = layoutInflater.inflate(R.layout.edit_goal_layout, null)
        mDialogView = dialogView

        val textInputLayout: TextInputLayout =
            dialogView.findViewById(R.id.goal_edit_text_input_layout)

        val dueDateCb: CheckBox = dialogView.findViewById(R.id.due_date_cb)
        val dueDateTv: TextView = dialogView.findViewById(R.id.edit_goal_due_date_tv)
        var dueDateCal: Calendar = Calendar.getInstance()
        val addToCalBtn: View = dialogView.findViewById(R.id.edit_goal_to_cal_tv)

        val cancelBtn: Button = dialogView.findViewById(R.id.edit_goal_cancel_button)
        val saveBtn: Button = dialogView.findViewById(R.id.edit_goal_save_button)

        val firstReminderEt: EditText = dialogView.findViewById(R.id.edit_reminder_title1)

        val firstDateTv: TextView = dialogView.findViewById(R.id.edit_reminder_date1)

        var isReminderSet = false
        cancelBtn.setOnClickListener {
            dialogBuilder.dismiss()
        }
        mDecision?.let {decision ->
            goal = decision.goals.getOrElse(goalPos) {
                decision.goals.add(0, goal)
                goal }

            textInputLayout.editText?.setText(goal.name)

            goal.epochDueDate?.let { epochDay ->
                dueDateCal =
                    GregorianCalendar.from(
                        LocalDate.ofEpochDay(epochDay).atStartOfDay(ZoneId.systemDefault())
                    )
            }

            if (goal.reminder.isActive) {
                isReminderSet = true
                firstReminderEt.setText(goal.reminder.title)
                firstDateTv.text = goal.reminder.time.format(
                    ofLocalizedDateTime(FormatStyle.SHORT)
                )
            }
            firstDateTv.setOnClickListener { tv ->
                openDateTimePicker(
                    goal.reminder,
                    tv as TextView
                )
                isReminderSet = true
            }
        }
        saveBtn.setOnClickListener {

            if (textInputLayout.editText?.length()!! <= textInputLayout.counterMaxLength) {
                saveGoal(textInputLayout, firstReminderEt, dueDateCb, dueDateCal, isReminderSet, goal )
                dialogBuilder.dismiss()
            } else {
                Toast.makeText(
                    mContext,
                    "Title length is limited to max ${textInputLayout.counterMaxLength}" +
                            " characters", Toast.LENGTH_SHORT
                ).show()
            }
        }
        dueDateCb.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                UiUtils.fadeInViews(dueDateTv, addToCalBtn)
            else
                UiUtils.fadeOutViews(dueDateTv, addToCalBtn)
        }
        dueDateTv.setOnClickListener {
            pickADueDate(dueDateCal, dueDateTv)
        }
        addToCalBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, dueDateCal.timeInMillis)
                .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
                .putExtra(CalendarContract.Events.TITLE, textInputLayout.editText?.text.toString())

            startActivity(intent)
        }
        dialogBuilder.setView(dialogView)
        dialogBuilder.show()
    }

    private fun saveGoal(
        textInputLayout: TextInputLayout,
        reminderEt: EditText,
        dueDateCb: CheckBox,
        dueDateCal: Calendar,
        reminderSet: Boolean,
        goal: Goal) {
        goal.name = textInputLayout.editText?.text.toString()
        goal.reminder.title = reminderEt.text.toString()
        if (dueDateCb.isChecked) {
            goal.epochDueDate =
                dueDateCal.time.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .toEpochDay()
        } else
            goal.epochDueDate = null
        viewModel.updateDecision(mDecision!!)
        if (reminderSet) AlarmScheduler.scheduleAlarmsForReminder(mContext, goal.reminder)
        (binding.goalsRv.adapter as GoalsAdapter).goalsList = mDecision!!.goals
    }

    private fun pickADueDate(
        dueDateCal: Calendar,
        dateTv: TextView
    ) {
        val dialogView = View.inflate(activity, R.layout.date_picker, null)
        val alertDialog =
            AlertDialog.Builder(requireActivity()).create()

        dialogView.findViewById<Button>(R.id.date_set)
            .setOnClickListener {
                val datePicker: DatePicker =
                    dialogView.findViewById(R.id.due_date_picker) as DatePicker
                dueDateCal.set(
                    datePicker.year,
                    datePicker.month,
                    datePicker.dayOfMonth
                )
                dateTv.text = dueDateCal.time.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .format(ofLocalizedDate(FormatStyle.SHORT))
                alertDialog.dismiss()
            }
        alertDialog.setView(dialogView)
        alertDialog.show()
    }


    private fun openDateTimePicker(
        reminderObj: ReminderObj?,
        dateTimeView: TextView
    ) {
        val dialogView = View.inflate(activity, R.layout.date_time_picker, null)
        val alertDialog =
            AlertDialog.Builder(requireActivity()).create()

        dialogView.findViewById<Button>(R.id.date_time_set)
            .setOnClickListener {
                val datePicker: DatePicker =
                    dialogView.findViewById(R.id.date_picker) as DatePicker
                val timePicker: TimePicker =
                    dialogView.findViewById(R.id.time_picker) as TimePicker
                val calendar: Calendar = GregorianCalendar(
                    datePicker.year,
                    datePicker.month,
                    datePicker.dayOfMonth,
                    timePicker.hour,
                    timePicker.minute
                )
                reminderObj?.isActive = true

                reminderObj?.time =
                    LocalDateTime.ofInstant(
                        calendar.toInstant(),
                        TimeZone.getDefault().toZoneId()
                    )
                dateTimeView.text =
                    reminderObj?.time?.format(ofLocalizedDate(FormatStyle.SHORT))
                alertDialog.dismiss()
            }
        alertDialog.setView(dialogView)
        alertDialog.show()
    }
}