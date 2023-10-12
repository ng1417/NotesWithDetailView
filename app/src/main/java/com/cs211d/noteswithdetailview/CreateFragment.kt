package com.cs211d.noteswithdetailview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.navigation.Navigation


class CreateFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView =  inflater.inflate(R.layout.fragment_create, container, false)

        val titleEditText : EditText = rootView.findViewById(R.id.enter_title_edittext)
        val detailsEditText = rootView.findViewById<EditText>(R.id.enter_details_edittext)

        rootView.findViewById<Button>(R.id.add_note_button).setOnClickListener {

            val title = titleEditText.text.toString()
            val details = detailsEditText.text.toString()

            if(title.isNotEmpty() && details.isNotEmpty()) {
                val bundle = Bundle().apply {
                    putString("noteTitle", title)
                    putString("noteDetails", details)
                }
                Navigation.findNavController(rootView)
                      .navigate(R.id.action_createFragment_to_list_fragment, bundle)

            }

        }

        return rootView
    }


}