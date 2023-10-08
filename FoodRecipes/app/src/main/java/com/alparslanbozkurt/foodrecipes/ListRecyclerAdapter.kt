package com.alparslanbozkurt.foodrecipes

import android.content.Context
import android.renderscript.ScriptGroup.Binding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.alparslanbozkurt.foodrecipes.databinding.RecyclerRowBinding
import org.w3c.dom.NameList

class ListRecyclerAdapter(val foodNameList: ArrayList<String>,val idList: ArrayList<Int>) : RecyclerView.Adapter<ListRecyclerAdapter.FoodHolder>() {

    class FoodHolder(val binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return FoodHolder(binding)
    }

    override fun getItemCount(): Int {
        return foodNameList.size
    }

    override fun onBindViewHolder(holder: FoodHolder, position: Int) {
        holder.binding.recyclerRowText.text = foodNameList[position]
        holder.itemView.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToRecipeFragment("recycler",idList[position])
            Navigation.findNavController(it).navigate(action)
        }
    }
}