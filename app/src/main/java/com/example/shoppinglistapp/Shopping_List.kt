package com.example.shoppinglistapp

import android.net.Uri
import android.provider.OpenableColumns
import android.text.BoringLayout
import android.text.Editable
import android.text.method.SingleLineTransformationMethod
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Brush
import kotlin.random.Random
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp

data class ShoppingItem(val id:Int, var name: String, var quantity: Int, var isEditing: Boolean = false)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListApp(){
    var sItem by remember { mutableStateOf(listOf<ShoppingItem>())}
    var ShowDialog by remember { mutableStateOf(false)}
    var itemName by remember { mutableStateOf("") }
    var itemQuantity by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.randomFaded())  // Iske liye i took help form chat gpt.

    )
    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ){
        Button(
            onClick = {ShowDialog = true},
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Add Item")
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ){
            items(sItem){
                item ->
                if(item.isEditing){
                    ShoppingItemEditor(item=item, onEditComplete ={
                        editedName, editedQuantity ->
                        sItem = sItem.map { it.copy(isEditing = false)}
                        val editedItem = sItem.find {it.id ==item.id}
                        editedItem?.let {
                            it.name = editedName
                            it.quantity = editedQuantity
                        }
                    } )
                }else{
                    ShoppingListItem(item=item,
                        onEditClick = {
                        // Finding out which edit button we are editing and changing its "Editing to true"
                        sItem = sItem.map{it.copy(isEditing = it.id==item.id) }
                    },
                        onDeleteClick = {
                        sItem = sItem-item
                    })
                }
            }

        }
    }
    if(ShowDialog){
        AlertDialog(onDismissRequest = { ShowDialog=false },
            confirmButton = {
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween ){
                                Button(onClick = {
                                    if(itemName.isNotBlank()){
                                        val newItem = ShoppingItem(
                                            id = sItem.size+1,
                                            name = itemName,
                                            quantity = itemQuantity.toInt()
                                        )
                                        sItem += newItem
                                        ShowDialog = false
                                        itemName = ""
                                    }
                                }) {
                                    Text("Add")
                                }
                                Button(onClick = {ShowDialog = false}) {
                                    Text("Cancel")
                                }
                            }
            },
            title = { Text("Shopping List")},
            text = {
                Column {
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = {itemName = it},
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                    OutlinedTextField(
                        value = itemQuantity,
                        onValueChange = {itemQuantity = it},
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }
        )
    }
}

// For this I took help from web.
fun Brush.Companion.randomFaded(): Brush {
    val random = Random.Default
    val startColor = Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 0.3f)
    val endColor = Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 0.3f)
    return Brush.verticalGradient(listOf(startColor, endColor))
}
@Composable
fun ShoppingItemEditor(item: ShoppingItem, onEditComplete:(String, Int)->Unit){
    var editedName by remember { mutableStateOf(item.name) }
    var editedQuantity by remember { mutableStateOf(item.quantity.toString()) }
    var iseditting by remember { mutableStateOf(item.isEditing)}
    Row (modifier = Modifier
        .fillMaxWidth()
        .background(Color.Gray)
        .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
        ){
        Column {
            BasicTextField(
                value = editedName,
                onValueChange = {editedName=it},
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp)
            )
            BasicTextField(
                value = editedQuantity,
                onValueChange = {editedQuantity=it},
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp)
            ){
                Button(onClick = {
                    iseditting =false
                    onEditComplete(editedName, editedQuantity.toIntOrNull() ?: 1)
                }) {
                    Text("Save")
                }

            }
        }
    }
}

@Composable
fun ShoppingListItem(
    item: ShoppingItem,
    onEditClick:() -> Unit,
    onDeleteClick: () ->Unit,
){
    Row (
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(
                border = BorderStroke(2.dp, Color.Blue),
                shape = RoundedCornerShape(20)
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(text = item.name, modifier = Modifier.padding(8.dp))
        Text(text = "Qty : ${item.quantity}", modifier = Modifier.padding(8.dp))
        Row (modifier = Modifier.padding(8.dp)){
            IconButton(onClick = onEditClick ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "This is the Edit button of any given row")

            }
            IconButton(onClick = onDeleteClick ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "This is the Delete button of any given row")

            }
        }
    }
}