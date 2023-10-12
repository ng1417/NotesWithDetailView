package com.cs211d.noteswithdetailview

import androidx.appcompat.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import java.io.PrintWriter
import java.io.File


class ListFragment : Fragment(), MenuProvider {

    private lateinit var noteList : MutableList<Note>
    private lateinit var recyclerView : RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_list, container, false)
        val menuHost : MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        noteList = NoteList.getInstance(requireContext()).noteList
        recyclerView = rootView.findViewById<RecyclerView>(R.id.note_title_list_recyclerview)
        recyclerView.adapter = NoteAdapter(noteList)


        if(noteList.isEmpty()) {
            // STEP 3: Add code to read in the notes from a file when the list is empty.
            // Fill up the noteList with the notes read in from the file.

        }

        // this happens when you come back from the CreateFragment
        if(arguments!=null) {
            val title = arguments!!.getString("noteTitle")
            val details = arguments!!.getString("noteDetails")
            if(title!=null && details!=null) {

                var max = -1
                for(note in noteList) {
                    if(note.id > max) {
                        max = note.id
                    }
                }
                val id = max+1
                noteList.add(Note(id, title!!, details!!))

                // STEP 2: Add code to store the new note in a file. Make sure you are appending
                // to the file and note replacing the contents.
            }
            arguments!!.clear()
        }

        return rootView
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.create_fragment -> {
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_list_fragment_to_createFragment)
                return true
            }
            // STEP 4: Add code for what happens when the user selects the "clear list" menu option.
            else -> return false
        }

    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.note_menu, menu)
    }

    private class NoteAdapter(private val noteList:MutableList<Note>) : RecyclerView.Adapter<NoteHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            return NoteHolder(layoutInflater, parent)
        }

        override fun onBindViewHolder(holder: NoteHolder, position: Int) {
            val note = noteList[position]
            holder.bind(note)

            holder.itemView.setOnClickListener {
                val bundle = Bundle().apply{
                    putInt("noteID", note.id)
                }
                Navigation.findNavController(holder.itemView)
                    .navigate(R.id.action_list_fragment_to_detailsFragment, bundle)
            }
        }
        override fun getItemCount(): Int {
            return noteList.size
        }
    }

   private class NoteHolder(inflater: LayoutInflater, parent:ViewGroup?) :
           RecyclerView.ViewHolder(inflater.inflate(R.layout.note_title_list_item, parent,  false)) {
               private var noteTitleTextView : TextView
               init {
                   noteTitleTextView = itemView.findViewById(R.id.note_title_item_textview)
               }
                fun bind(note: Note) {
                    noteTitleTextView.text = note.title
                }
           }
}