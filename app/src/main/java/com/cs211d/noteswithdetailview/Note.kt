package com.cs211d.noteswithdetailview

class Note (var id : Int, var title : String, var details : String) {

    override fun toString(): String {
        return "$id $title $details"
    }

}