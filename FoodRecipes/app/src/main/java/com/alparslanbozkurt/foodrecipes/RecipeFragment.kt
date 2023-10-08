package com.alparslanbozkurt.foodrecipes

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.navigation.Navigation
import com.alparslanbozkurt.foodrecipes.databinding.FragmentRecipeBinding
import java.io.ByteArrayOutputStream

@Suppress("DEPRECATION")
class RecipeFragment : Fragment() {

    var selectedImage : Uri? = null
    var selectedBitmap : Bitmap? = null

    private lateinit var bindingRecipe : FragmentRecipeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bindingRecipe = FragmentRecipeBinding.inflate(inflater, container, false)


        return bindingRecipe.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindingRecipe.buttonSave.setOnClickListener {
            save(it)//function defined
        }
        bindingRecipe.imageView.setOnClickListener {
            select_image(it)//function defined
        }

        arguments?.let {
            var comingInformation = RecipeFragmentArgs.fromBundle(it).info
            if(comingInformation.equals("menu")){
                //came to add a new dish
                bindingRecipe.foodNameText.setText("")
                bindingRecipe.foodRecipeText.setText("")
                bindingRecipe.buttonSave.visibility = VISIBLE

                val imageSelectionBackground = BitmapFactory.decodeResource(context?.resources,R.drawable.uploadimage)
                bindingRecipe.imageView.setImageBitmap(imageSelectionBackground)
            }else{
                //came to see the previously created dish
                bindingRecipe.buttonSave.visibility = INVISIBLE
                val selectedId = RecipeFragmentArgs.fromBundle(it).id
                context?.let {
                    try {

                        val db = it.openOrCreateDatabase("Foods",Context.MODE_PRIVATE,null)
                        val cursor = db.rawQuery("SELECT * FROM foods WHERE id = ?", arrayOf(selectedId.toString()))

                        val foodNameIndex = cursor.getColumnIndex("foodname")
                        val foodIngeredientsIndex = cursor.getColumnIndex("foodingredients")
                        val foodRecipeIndex = cursor.getColumnIndex("foodrecipe")
                        val foodImage = cursor.getColumnIndex("image")

                        while (cursor.moveToNext()){
                            bindingRecipe.foodNameText.setText(cursor.getString(foodNameIndex))
                            bindingRecipe.foodIngredientsText.setText(cursor.getString(foodIngeredientsIndex))
                            bindingRecipe.foodRecipeText.setText(cursor.getString(foodRecipeIndex))
                            val byteArray = cursor.getBlob(foodImage)
                            val bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                            bindingRecipe.imageView.setImageBitmap(bitmap)
                        }
                        cursor.close()

                    }catch (e : Exception){
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun save (view: View){
        val foodName = bindingRecipe.foodNameText.text.toString()
        val foodIngredients = bindingRecipe.foodIngredientsText.text.toString()
        val foodRecipe: String = bindingRecipe.foodRecipeText.text.toString()

        if (selectedBitmap != null){
            val smallBitmap = smallBitmapCreate(selectedBitmap!!,300)
            //Bitmap to data conversion
            val outPutStream = ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG,50,outPutStream)
            val byteArray = outPutStream.toByteArray()

            try {
                //SQLite code
                context?.let {
                    val database = it.openOrCreateDatabase("Foods", Context.MODE_PRIVATE,null)
                    database.execSQL("CREATE TABLE IF NOT EXISTS foods (id INTEGER PRIMARY KEY, foodname VARCHAR, foodingredients VARCHAR, foodrecipe VARCHAR, image BLOB)")
                    //used statement because it was val
                    val sqlString = "INSERT INTO foods (foodname, foodingredients, foodrecipe, image) VALUES(? , ?, ?, ?)"
                    val statement = database.compileStatement(sqlString)
                    statement.bindString(1,foodName)
                    statement.bindString(2,foodIngredients)
                    statement.bindString(3,foodRecipe)
                    statement.bindBlob(4,byteArray)
                    statement.execute()//SQLite work

                }
            }catch (e : Exception){
                e.printStackTrace()
            }

            //to go back after receiving the content
            val action = RecipeFragmentDirections.actionRecipeFragmentToListFragment()
            Navigation.findNavController(view).navigate(action)
        }

    }

    fun select_image(view: View){

        activity?.let {
            if (ContextCompat.checkSelfPermission(it.applicationContext,Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                //ContextCompat -> do not need to check all API versions    checkSelfPermission -> has the permit been checked
                //Select the android one next to the manifest       !!!not authorised, request permission
                requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_IMAGES),1)

            }else{//permission granted, go to the gallery without asking permission
                val galeryIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeryIntent,2)

            }
        }
    }


    override fun onRequestPermissionsResult(//the function is configured according to the state of the permissions
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode ==1){
            if(grantResults.size > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                //authorisation received
                val galeryIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeryIntent,2)
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //authorisation granted
        if(requestCode == 2 && resultCode == Activity.RESULT_OK && data != null){
            selectedImage = data.data
            //convert the data to bitmap
            try {
                context?.let {
                    if (selectedImage != null ){
                        if (Build.VERSION.SDK_INT >= 28){//related to API version
                            val source = ImageDecoder.createSource(it.contentResolver,selectedImage!!)
                            selectedBitmap = ImageDecoder.decodeBitmap(source)
                            bindingRecipe.imageView.setImageBitmap(selectedBitmap)
                        }else{//related to API version
                            selectedBitmap = MediaStore.Images.Media.getBitmap(it.contentResolver,selectedImage)
                            bindingRecipe.imageView.setImageBitmap(selectedBitmap)
                        }
                    }
                }


            }catch (e : Exception){
                e.printStackTrace()
            }
        }


        super.onActivityResult(requestCode, resultCode, data)
    }

    fun smallBitmapCreate(userSelectedBitmap : Bitmap, maxSize : Int) : Bitmap{
        var width = userSelectedBitmap.width
        var height = userSelectedBitmap.height

        val bitmapRatio : Double = width.toDouble() / height.toDouble()
        if (bitmapRatio > 1){
            //visual landspace
            width = maxSize
            val abbreviatedHeight = width / bitmapRatio
            height = abbreviatedHeight.toInt()
        }
        else{
            //visual vertical
            height = maxSize
            val abbreviatedWidth = height / bitmapRatio
            width = abbreviatedWidth.toInt()
        }
        return Bitmap.createScaledBitmap(userSelectedBitmap,width,height,true)
    }

}