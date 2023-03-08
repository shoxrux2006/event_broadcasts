package uz.gita.eventapp.presentation.ui.screen.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.gita.eventapp.R
import uz.gita.eventapp.databinding.DialogInfoBinding
import uz.gita.eventapp.databinding.ScreenMainBinding

import uz.gita.eventapp.presentation.adapter.EventAdapter
import uz.gita.eventapp.presentation.ui.viewmodel.MainViewModel
import uz.gita.eventapp.presentation.ui.viewmodel.impl.MainViewModelImpl
import uz.gita.eventapp.service.EventService

@AndroidEntryPoint
class MainScreen : Fragment(R.layout.screen_main) {

    private val binding: ScreenMainBinding by viewBinding(ScreenMainBinding::bind)
    private val viewModel: MainViewModel by viewModels<MainViewModelImpl>()
    private val adapter: EventAdapter by lazy { EventAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allEvents.collectLatest {
                adapter.submitList(it)
            }


        }
        adapter.setSwitchChangedListener {
            viewModel.changeState(it)
        }

        binding.info.setOnClickListener {
            val dialog = BottomSheetDialog(requireContext(), R.style.SheetDialog)
            val dialogView: View =
                LayoutInflater.from(requireContext())
                    .inflate(R.layout.bottom_dialog_info, null, false)
            dialog.setContentView(dialogView)
            dialog.show()

            dialogView.findViewById<View>(R.id.view2).setOnClickListener {
                showInfo()
                dialog.cancel()
            }
            dialogView.findViewById<View>(R.id.view1).setOnClickListener {
                ShareCompat.IntentBuilder.from(requireActivity())
                    .setType("text/plain")
                    .setChooserTitle("Chooser title")
                    .setText("http://play.google.com/store/apps/details?id=" + activity?.packageName)
                    .startChooser();
                dialog.cancel()
            }

        }


        binding.rv.adapter = adapter

        val intent = Intent(requireContext(), EventService::class.java)
        requireActivity().startService(intent)
    }


    private fun showInfo() {
        val dialog = AlertDialog.Builder(requireContext()).create()
        val dialogBinding =
            DialogInfoBinding.inflate(LayoutInflater.from(requireContext()), null, false)

        dialogBinding.enter.setOnClickListener {
            dialog.cancel()
        }
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setView(dialogBinding.root)
        dialog.show()
    }

}