package com.cs211d.noteswithdetailview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class DetailsFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_details, container, false)

        val noteID = arguments?.getInt("noteID")!! // we are assuming noteID is not null because arguments is not null
        val note = NoteList.getInstance(requireContext()).getNote(noteID)

        if(note!=null) {
            val titleTextView = rootView.findViewById<TextView>(R.id.note_title_textview)
            val detailsTextView = rootView.findViewById<TextView>(R.id.note_details_textview)

            titleTextView.text = note!!.title
            detailsTextView.text = note!!.details
        }



        return rootView
    }


}