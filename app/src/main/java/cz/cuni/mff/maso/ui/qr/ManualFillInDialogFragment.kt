package cz.cuni.mff.maso.ui.qr

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import cz.cuni.mff.maso.R
import cz.cuni.mff.maso.api.RequestTypeEnum
import cz.cuni.mff.maso.databinding.DialogManualFillInBinding
import cz.cuni.mff.maso.tools.showKeyboardDelayed

interface ManualFillInDialogListener {
	fun onDataEntered(teamNo: Int, problemNo: Int, requestType: RequestTypeEnum)
	fun onDismissed()
}

interface ManualFillInView {
	fun cancelOverlay()
	fun onSubmitClicked()
}

class ManualFillInDialogFragment : DialogFragment() {

	companion object {
		val TAG = ManualFillInDialogFragment::class.java.simpleName

		fun newInstance() = ManualFillInDialogFragment()
	}

	val viewModel by lazy { ViewModelProviders.of(this).get(ManualFillInViewModel::class.java) }
	lateinit var listener: ManualFillInDialogListener
	private lateinit var binding: DialogManualFillInBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		isCancelable = true
		// set callback listener
		try {
			listener = activity as ManualFillInDialogListener
		} catch (e: ClassCastException) {
			throw ClassCastException(activity!!.toString() + " must implement " + ManualFillInDialogListener::class.java.name)
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		binding = DialogManualFillInBinding.inflate(inflater, container, false)
		binding.lifecycleOwner = this
		binding.viewModel = viewModel
		binding.view = object : ManualFillInView {
			override fun cancelOverlay() {
				dismiss()
			}

			override fun onSubmitClicked() {
				val teamNumber = viewModel.teamNumber.value?.toIntOrNull()
				val taskNumber = viewModel.taskNumber.value?.toIntOrNull()
				if (teamNumber != null && taskNumber != null) {
					val requestType = when(binding.spinnerSelector.selectedItemPosition) {
						0 -> RequestTypeEnum.SOLVE
						1 -> RequestTypeEnum.EXCHANGE
						else -> RequestTypeEnum.CANCEL
					}
					listener.onDataEntered(teamNumber, taskNumber, requestType)
					dismiss()
				} else {
					Toast.makeText(context, R.string.error_invalid_data, Toast.LENGTH_LONG).show()
				}
			}
		}
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
		binding.teamNumberInput.showKeyboardDelayed()
		context?.let { initSpinner(it) }
	}

	private fun initSpinner(context: Context) {
		val options = resources.getStringArray(R.array.request_options)
		val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, options)
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
		binding.spinnerSelector.adapter = adapter
	}

	override fun onDismiss(dialog: DialogInterface) {
		super.onDismiss(dialog)
		listener.onDismissed()
	}

	override fun onCancel(dialog: DialogInterface) {
		super.onCancel(dialog)
		listener.onDismissed()
	}
}