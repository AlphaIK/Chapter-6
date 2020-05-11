package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.byted.camp.todolist.db.TodoDbHelper;
import com.byted.camp.todolist.db.TodoContract.noteEntry;

public class NoteActivity extends AppCompatActivity {
    TodoDbHelper tododbhelper;
    private int priority = 1;
    CheckBox check1;
    CheckBox check2;

    private EditText editText;
    private Button addBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        setTitle(R.string.take_a_note);

        tododbhelper = new TodoDbHelper(this);
        check1 = findViewById(R.id.check1);
        check2 = findViewById(R.id.check2);

        editText = findViewById(R.id.edit_text);
        editText.setFocusable(true);
        editText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(editText, 0);
        }

        addBtn = findViewById(R.id.btn_add);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence content = editText.getText();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(NoteActivity.this,
                            "No content to add", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean succeed = saveNote2Database(content.toString().trim());
                if (succeed) {
                    Toast.makeText(NoteActivity.this,
                            "Note added", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                } else {
                    Toast.makeText(NoteActivity.this,
                            "Error", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });

        check1.setOnCheckedChangeListener(new checkListener());
        check2.setOnCheckedChangeListener(new checkListener());
    }

    private class checkListener implements CompoundButton.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            String text = buttonView.getText().toString();
            if(!isChecked){
                priority = 1;
                return;
            }
            if(check1.isChecked()){
                priority = 2;
                check2.setChecked(false);
            }
            if(check2.isChecked()){
                priority = 3;
                check1.setChecked(false);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean saveNote2Database(String content) {
        // TODO 插入一条新数据，返回是否插入成功
        SQLiteDatabase db = tododbhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(noteEntry.TABLE_DATE,System.currentTimeMillis());
        contentValues.put(noteEntry.TABLE_STATE, 0);
        contentValues.put(noteEntry.TABLE_CONTENT,content);
        contentValues.put(noteEntry.TABLE_PRIORITY,priority);
        return db.insert(noteEntry.TABLE_NAME,null, contentValues) != -1;

    }
}
