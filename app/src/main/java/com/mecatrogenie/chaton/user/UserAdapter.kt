package com.mecatrogenie.chaton.user

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.mecatrogenie.chaton.R

class UserAdapter(initialUsers: List<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>(), Filterable {

    var onItemClick: ((User) -> Unit)? = null
    private var users: MutableList<User> = initialUsers.toMutableList()
    private var usersFiltered: MutableList<User> = initialUsers.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = usersFiltered[position]
        holder.userName.text = user.displayName
        holder.userIcon.load(user.photoUrl) {
            crossfade(true)
            placeholder(R.drawable.ic_default_profile)
        }
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(user)
        }
    }

    override fun getItemCount(): Int {
        return usersFiltered.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateUsers(newUsers: List<User>) {
        users.clear()
        users.addAll(newUsers)
        usersFiltered.clear()
        usersFiltered.addAll(newUsers)
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                val filteredList = if (charString.isEmpty()) {
                    users
                } else {
                    users.filter {
                        it.displayName?.contains(charString, true) == true
                    }
                }
                return FilterResults().apply { values = filteredList }
            }

            @SuppressLint("NotifyDataSetChanged")
            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                usersFiltered.clear()
                usersFiltered.addAll(results?.values as List<User>)
                notifyDataSetChanged()
            }
        }
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userIcon: ImageView = itemView.findViewById(R.id.user_icon)
        val userName: TextView = itemView.findViewById(R.id.user_name)
    }
}
