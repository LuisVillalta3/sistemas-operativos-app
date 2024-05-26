package sv.edu.udb.vr181981.appsistemasoperativos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var listViewMessages: ListView
    private lateinit var editTextMessage: EditText
    private lateinit var messagesAdapter: ArrayAdapter<String>
    private val messagesList = ArrayList<String>()
    private val database = FirebaseDatabase.getInstance().getReference("messages")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listViewMessages = findViewById(R.id.listViewMessages)
        editTextMessage = findViewById(R.id.editTextMessage)

        messagesAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, messagesList)
        listViewMessages.adapter = messagesAdapter

        editTextMessage.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                true
            } else {
                false
            }
        }

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messagesList.clear()
                for (messageSnapshot in snapshot.children) {
                    val message = messageSnapshot.getValue(String::class.java)
                    if (message != null) {
                        messagesList.add(message)
                    }
                }
                messagesAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun sendMessage() {
        val message = editTextMessage.text.toString()
        if (message.isNotEmpty()) {
            database.push().setValue(message)
            editTextMessage.text.clear()
        }
    }
}