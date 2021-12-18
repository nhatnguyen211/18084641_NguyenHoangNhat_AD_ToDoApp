package com.example.todoapp

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.Extensions.toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_recyclerview_todoapp.*
import kotlinx.android.synthetic.main.add_new_task.*
import kotlinx.android.synthetic.main.add_new_task.view.*
import kotlinx.android.synthetic.main.update_new_task.*
import kotlinx.android.synthetic.main.update_new_task.view.*


class MainActivity : AppCompatActivity() {

    lateinit var signInEmail: String
    lateinit var signInPassword: String
    lateinit var signInInputsArray: Array<EditText>

    private var arrayList = ArrayList<Work>()
    private val check:IntArray= IntArray(10000)
    var count:Long =0
    private lateinit var databaseReference: DatabaseReference
    private var note=-1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview_todoapp)

        check()
        //
        login()
        //
        loadData()
        //
        showDialog.setOnClickListener {
            add()
        }

        //
        imgChecked.setOnClickListener{
            var newList=arrayList
            for(item in check.indices){
                if (check[item]==1){
                    if (newList[item].checked=="0")
                        newList[item].checked="1"
                    else newList[item].checked="0"
                }
                check[item]=-1
            }
            check()

            loadRecyclerView()
            themVaoFireBase()
        }
        //
        imgEdit.setOnClickListener{

            var dem=0
            for (item in check.indices){
                if (check[item]==1)
                    dem++
            }
            if (dem>1){
                toast("Chọn quá nhiều!")
            }

            else{
                if (dem==0){
                    toast("Chọn công việc!")

                }
                else{
                    for (item in check.indices){
                        if (check[item]==1){
                            note=item

                        }

                    }
                    if (note!=-1)
                        showDialogUpdate(note)
                    else toast("Chọn côn việc!")

                }
            }

        }

        imgDelete.setOnClickListener{
            var newList=ArrayList<Work>()
            for(item in arrayList.indices){
                if (check[item]!=1)
                    newList.add(arrayList[item])
                else check[item]==-1
            }

            arrayList=newList
            check()

            loadRecyclerView()
            themVaoFireBase()
        }


    }
    private  fun showDialogUpdate(index:Int)
    {


        val mDialogView = LayoutInflater.from(this).inflate(R.layout.update_new_task, null)
        var mBuilder = AlertDialog.Builder(this@MainActivity)
        mBuilder.setView(mDialogView)
        if(index!=-1)
            mDialogView.etWork_Update.setText(arrayList[index].work.toString())
        val mAlertDialog = mBuilder.show()
        mAlertDialog.btnSave_Update.setOnClickListener {
            mAlertDialog.dismiss()
            val check = arrayList[index].checked.toString()
            val work = mDialogView.etWork_Update.text.toString()
            val user = Work(work,check)
            arrayList.set(index,user)

            check()

            loadRecyclerView()
            themVaoFireBase()
        }
        mDialogView.btnCancel_Update.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }
    private fun add()
    {

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.add_new_task, null)
        var mBuilder = AlertDialog.Builder(this@MainActivity)
        mBuilder.setView(mDialogView)

        val mAlertDialog = mBuilder.show()
        mAlertDialog.btnSave.setOnClickListener {
            mAlertDialog.dismiss()
            val work = mDialogView.etWork.text.toString()
            val user = Work(work)
            arrayList.add(user)

            check()
            loadRecyclerView()
            themVaoFireBase()
        }
        mDialogView.btnCancel.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }
    private fun loadRecyclerView(){
        check()
        var adapter = Apdapter(arrayList)
        rcvWork.adapter = adapter
        rcvWork.layoutManager = LinearLayoutManager(this)
        rcvWork.setHasFixedSize(true)
        adapter.ItemClickListener={position, checked, cardView ->
            cardView.setOnClickListener{

                cardView.isSelected=!cardView.isSelected
                if (cardView.isSelected){
                    check[position]=1

                }
                else{
                    check[position]=-1

                }
            }
        }
        adapter.updateList(arrayList)
    }






    fun themVaoFireBase(){
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        val uid = FirebaseUtils.firebaseAuth.currentUser?.uid

        if (uid!=null){
            databaseReference.child(uid).setValue(arrayList).addOnCompleteListener {
                if (it.isSuccessful){
                    toast("Successfully")
                }else{
                    toast("Failed")
                }
            }
        }
    }
    fun loadData(){
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        val uid = FirebaseUtils.firebaseAuth.currentUser?.uid

        if (uid!=null){
            databaseReference.child(uid).get().addOnSuccessListener {
                count=it.childrenCount
                for (i in count downTo 1){

                    var work = it.child("${count-i}").child("work").value.toString()
                    var checked = it.child("${count-i}").child("checked").value.toString()
                    var user = Work(work,checked)
                    arrayList.add(user)
                }
                loadRecyclerView()


            }

        }

    }
    fun check(){
        for(i in 0..100)
            check[i]=-1
    }
    private fun notEmpty(): Boolean = signInEmail.isNotEmpty() && signInPassword.isNotEmpty()
    private fun login() {
        signInEmail = "nhat@gmail.com"
        signInPassword = "123456"

        if (notEmpty()) {
            FirebaseUtils.firebaseAuth.signInWithEmailAndPassword(signInEmail, signInPassword)
                .addOnCompleteListener { signIn ->
                    if (signIn.isSuccessful) {

                        toast("signed in successfully")

                    } else {
                        toast("sign in failed")
                    }
                }
        } else {
            signInInputsArray.forEach { input ->
                if (input.text.toString().trim().isEmpty()) {
                    input.error = "${input.hint} is required"
                }
            }
        }
    }
}