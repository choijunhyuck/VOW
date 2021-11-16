package com.vowcompany.vow.feed

class BriefFeedListItem(id: String, created_at: String) {

    var id: String
        internal set
    var created_at: String
        internal set

    init {
        this.id = id
        this.created_at = created_at
    }

}