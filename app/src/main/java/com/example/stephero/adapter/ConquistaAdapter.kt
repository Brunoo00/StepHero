package com.example.stephero.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stephero.databinding.ItemConquistaBinding
import com.example.stephero.model.Conquista

class ConquistaAdapter(
    private val conquistas: List<Conquista>
) : RecyclerView.Adapter<ConquistaAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemConquistaBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(conquistas[position])
    }

    override fun getItemCount(): Int = conquistas.size

    inner class ViewHolder(val binding: ItemConquistaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(conquista: Conquista) {
            binding.txtTitulo.text = conquista.titulo
            binding.txtDescricao.text = conquista.descricao
        }
    }
}