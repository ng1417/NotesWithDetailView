package com.cs211d.noteswithdetailview

import androidx.appcompat.app.AlertDialog
import android.R.string
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.NonDisposableHandle.parent
import java.io.*

const val NOTE_FILE = "notes.txt"
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
        val outputStream: FileOutputStream

        if(noteList.isEmpty()) {
            // STEP 3: Add code to read in the notes from a file when the list is empty.
            // Fill up the noteList with the notes read in from the file.

            try {
                val fileInputStream = requireContext().openFileInput(NOTE_FILE)
                val inputStreamReader = InputStreamReader(fileInputStream)
                val reader = BufferedReader(inputStreamReader)

                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    val noteLine = line?.split(",")
                    if (noteLine != null) {
                        val id = noteLine[0].toInt()
                        val title = noteLine[1]
                        val details = noteLine[2]
                        val note = Note(id, title, details)
                        noteList.add(note)
                    }
                }

                reader.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }}


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

                //put list into csv format
                val noteListString = noteList.joinToString("\n") { "${it.id},${it.title},${it.details}" }

                //write to file
                try {
                    outputStream = requireContext().openFileOutput(NOTE_FILE, Context.MODE_PRIVATE)
                    outputStream.write(noteListString.toByteArray())
                    outputStream.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            arguments!!.clear()
        }
        return rootView
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val file = File(requireContext().filesDir, NOTE_FILE)
            when(item.itemId) {
                R.id.create_fragment -> {
                    Navigation.findNavController(requireView())
                        .navigate(R.id.action_list_fragment_to_createFragment)
                    return true
                }
                // STEP 4: Add code for what happens when the user selects the "clear list" menu option.
                R.id.clear_text -> {
                    val builder = AlertDialog.Builder(requireActivity())
                    builder.setMessage(R.string.text_confirm_dialog)
                        .setPositiveButton("Yes") { dialog, id ->
                            //checking for file
                            if (file.exists()) {
                                //deleting file if exists
                                file.delete()
                                //clearing list
                                noteList.clear()
                                //calling recyclerView to update it too
                                recyclerView.adapter?.notifyDataSetChanged()
                            }
                        }
                        //return true
                        .setNegativeButton("No") { dialog, id ->
                            dialog.dismiss()
                        }
                    builder.create().show()
                    return true
                }
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