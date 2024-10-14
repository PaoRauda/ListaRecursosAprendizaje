package edu.udb.recursoaprendizaje

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ResourceAdapter(private val resources: List<Resource>) : RecyclerView.Adapter<ResourceAdapter.ViewHolder>() {
    private var onItemClick: OnItemClickListener? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.tvName)
        val typeTextView: TextView = view.findViewById(R.id.tvType)
        val urlTextView: TextView = view.findViewById(R.id.tvUrl)
        val descriptionTextView: TextView = view.findViewById(R.id.tvDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recurso_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val resource = resources[position]
        holder.nameTextView.text = resource.name
        holder.typeTextView.text = resource.type
        holder.urlTextView.text = resource.url
        holder.descriptionTextView.text = resource.description

        // Listener del elemento de la lista
        holder.itemView.setOnClickListener {
            onItemClick?.onItemClick(resource)
        }
    }

    override fun getItemCount(): Int {
        return resources.size
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClick = listener
    }

    interface OnItemClickListener {
        fun onItemClick(resource: Resource)
    }
}