package com.fredrikbogg.android_chat_app.ui.settings

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fredrikbogg.android_chat_app.App
import com.fredrikbogg.android_chat_app.R
import com.fredrikbogg.android_chat_app.databinding.FragmentSettingsBinding
import com.fredrikbogg.android_chat_app.data.EventObserver
import com.fredrikbogg.android_chat_app.util.SharedPreferencesUtil
import com.fredrikbogg.android_chat_app.util.convertFileToByteArray
import kotlinx.android.synthetic.main.fragment_settings.*
import android.view.View
import android.view.ContextMenu
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil.setContentView
import com.fredrikbogg.android_chat_app.Video


class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModels { SettingsViewModelFactory(App.myUserID) }

    private lateinit var viewDataBinding: FragmentSettingsBinding
    private val selectImageIntentRequestCode = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        viewDataBinding = FragmentSettingsBinding.inflate(inflater, container, false)
            .apply { viewmodel = viewModel }
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setHasOptionsMenu(true)

        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupObservers()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack()
                return true
            }
            R.id.item_1 -> true
            R.id.item_2 -> true
            R.id.item_3 -> true
            R.id.item_4 -> true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == selectImageIntentRequestCode) {
            data?.data?.let { uri ->
                convertFileToByteArray(requireContext(), uri).let {
                    viewModel.changeUserImage(it)
                }
            }
        }
    }

    private fun setupObservers() {
        viewModel.editStatusEvent.observe(viewLifecycleOwner,
            EventObserver { showEditStatusDialog() })

        viewModel.editImageEvent.observe(viewLifecycleOwner,
            EventObserver { startSelectImageIntent() })

        viewModel.logoutEvent.observe(viewLifecycleOwner,
            EventObserver {
                SharedPreferencesUtil.removeUserID(requireContext())
                navigateToStart()
            })
    }

    private fun showEditStatusDialog() {
        val input = EditText(requireActivity() as Context)
        AlertDialog.Builder(requireActivity()).apply {
            setTitle("Status:")
            setView(input)
            setPositiveButton("Ok") { _, _ ->
                val textInput = input.text.toString()
                if (!textInput.isBlank() && textInput.length <= 40) {
                    viewModel.changeUserStatus(textInput)
                }
            }
            setNegativeButton("Cancel") { _, _ -> }
            show()
        }
    }

    private fun startSelectImageIntent() {
        val selectImageIntent = Intent(Intent.ACTION_GET_CONTENT)
        selectImageIntent.type = "image/*"
        startActivityForResult(selectImageIntent, selectImageIntentRequestCode)
    }

    private fun navigateToStart() {
        findNavController().navigate(R.id.action_navigation_settings_to_startFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        /*
        videollamada.setOnClickListener {
            activity?.let{
                val intent = Intent (it, Video::class.java)
                it.startActivity(intent)
            }

        }*/
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_item, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }




/*
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_settings)
        registerForContextMenu(btnPopup)

    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.menu_item,menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.item_1 -> Toast.makeText(this,"Item 1", Toast.LENGTH_SHORT).show()
            R.id.item_2 -> Toast.makeText(this,"Item 2", Toast.LENGTH_SHORT).show()
            R.id.item_3 -> Toast.makeText(this,"Item 3", Toast.LENGTH_SHORT).show()
            R.id.item_4 -> Toast.makeText(this,"Item 4", Toast.LENGTH_SHORT).show()
        }
        return super.onContextItemSelected(item)
    }

*/







}