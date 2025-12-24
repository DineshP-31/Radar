package com.dp.radar.com.dp.radar.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Study {
    var x= 0
    fun launchTest(){

        GlobalScope.launch {
            print()
        }
        Log.e("Ptsb", "Dinesh"+x)
    }

    suspend fun print(){
        delay(2000)
        x = 1
        Log.e("Ptsb", "Delayed")
    }
}

fun <A> Collection<A>.Sort(){

}

@SuppressLint("UnrememberedMutableState")
@Composable
fun TestSideEffectsApi() {
    val x =  mutableStateOf(1)

    SideEffect  {
        for(i in 0..100){
            //delay(1000)
            x.value += i
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            text = "Hello ${x.value}"
        )
    }
}