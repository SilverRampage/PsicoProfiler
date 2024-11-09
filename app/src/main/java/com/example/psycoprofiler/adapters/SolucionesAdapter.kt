package com.example.psycoprofiler.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.psycoprofiler.R

class SolucionesAdapter(private val soluciones: List<String>) : RecyclerView.Adapter<SolucionesAdapter.SolucionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SolucionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_solution, parent, false)
        return SolucionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SolucionViewHolder, position: Int) {
        val solucion = soluciones[position]
        holder.tvSolution.text = solucion
    }

    override fun getItemCount(): Int {
        return soluciones.size
    }

    class SolucionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSolution: TextView = itemView.findViewById(R.id.tvSolution)
    }
}
