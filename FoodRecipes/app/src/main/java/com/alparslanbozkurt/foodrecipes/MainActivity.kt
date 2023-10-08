package com.alparslanbozkurt.foodrecipes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.Navigation
import com.alparslanbozkurt.foodrecipes.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //the process of connecting the menu is performed
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.add_food,menu)


        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //what to do if something is selected from the options menu
        if (item.itemId == R.id.add_food_item){
            //the if structure, we code the action to be taken if the item is selected
            val action = ListFragmentDirections.actionListFragmentToRecipeFragment("menu",0)
            Navigation.findNavController(this,R.id.fragmentContainerView2).navigate(action)
        }
        return super.onOptionsItemSelected(item)
    }
}