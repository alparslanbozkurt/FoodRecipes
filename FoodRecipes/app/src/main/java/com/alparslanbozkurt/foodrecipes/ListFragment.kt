package com.alparslanbozkurt.foodrecipes

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alparslanbozkurt.foodrecipes.databinding.FragmentListBinding

class ListFragment : Fragment() {

    var foodNameList = ArrayList<String>()
    var foodIdList = ArrayList<Int>()
    private lateinit var listRecyclerAdapter: ListRecyclerAdapter

    private lateinit var bindingList: FragmentListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bindingList = FragmentListBinding.inflate(inflater , container, false)

        return bindingList.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sqlDataRetrieval()
        listRecyclerAdapter = ListRecyclerAdapter(foodNameList,foodIdList)
        bindingList.recyclerView.layoutManager = LinearLayoutManager(context)
        bindingList.recyclerView.adapter = listRecyclerAdapter


    }

    fun sqlDataRetrieval(){
        try {
            activity?.let {
                val dataBase = it.openOrCreateDatabase("Foods",Context.MODE_PRIVATE,null)

                val cursor = dataBase.rawQuery("SELECT * FROM foods",null)
                val idIndex = cursor.getColumnIndex("id")
                val foodNameIndex = cursor.getColumnIndex("foodname")

                foodNameList.clear()
                foodIdList.clear()
                while (cursor.moveToNext()){
                    foodNameList.add(cursor.getString(foodNameIndex))
                    foodIdList.add(cursor.getInt(idIndex))
                }
                listRecyclerAdapter.notifyDataSetChanged()
                cursor.close()
            }
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

}