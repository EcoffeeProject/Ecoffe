package com.example.ecoffe;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class JoinActivity extends AppCompatActivity implements Serializable {


    Intent intent;

    private EditText et_id, et_pw, check_pw;
    private Button btn_join, checkBT;
    private AlertDialog dialog;
    private boolean validate = false , checkPW = false;
    private static String TAG = "서버와의 연결 확인중 : ";

    protected void onCreate(Bundle savedInstanceStare) {
        super.onCreate(savedInstanceStare);
        setContentView(R.layout.activity_join);


        //ID값 중복확인
        et_id = findViewById(R.id.et_id);
        et_pw = findViewById(R.id.et_pw);
        check_pw = findViewById(R.id.check_pw);
        btn_join = findViewById(R.id.btn_join);
        checkBT = findViewById(R.id.checkBT);


        checkBT.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String userID = et_id.getText().toString();
                if (validate) {
                    return; //검증 완료
                }

                if (userID.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    dialog = builder.setMessage("아이디를 입력하세요.").setPositiveButton("확인", null).create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {

                                Toast.makeText(getApplicationContext(),"사용할 수 있는 아이디입니다.",Toast.LENGTH_SHORT).show();
                                et_id.setEnabled(false); //아이디값 고정
                                validate = true; //검증 완료

                            }
                            else {

                                Toast.makeText(getApplicationContext(),"이미 존재하는 아이디입니다.",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                };
                ValidateRequest validateRequest = new ValidateRequest(userID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(JoinActivity.this);
                queue.add(validateRequest);
            }
        });

        //회원가입 버튼 클릭시 수행행
        btn_join = findViewById(R.id.btn_join);
        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //check pw 랑 pw입력이랑 같을 경우


                //EditText에 현재 입력되어 있는 값을 get해옴옴
                String userID = et_id.getText().toString();
                String userPassword = et_pw.getText().toString();
                String checkPassword = check_pw.getText().toString();


                Response.Listener<String> responseListener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        if(checkPassword.equals(userPassword)) {
                            checkPW = true;
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");
                                if (success) { //회원가입 성공한 경우
                                    Toast.makeText(getApplicationContext(), "회원가입을 완료하였습니다.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish(); //회원가입 완료하면 액티비티 종료

                                } else {//회원가입 실패한 경우
                                    Toast.makeText(getApplicationContext(), "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else if (userID.equals("") || userPassword.equals("") || checkPassword.equals("")) {
                            Toast.makeText(getApplicationContext(), "모두 기입하였는지 한번 더 확인해주세요.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "비밀번호가 일치하는지 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                            checkPW = false;
                        }


                    }
                };


                if(userID.equals("") || userPassword.equals("") || checkPassword.equals("")) {
                    Toast.makeText(getApplicationContext(), "모두 기입하였는지 한번 더 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(validate == false){
                    Toast.makeText(getApplicationContext(), "ID중복확인을 진행해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if( checkPassword.equals(userPassword)) {
                    //서버로 Volley를 이용해서 요청을 함.
                    JoinRequest joinRequest = new JoinRequest(userID, checkPassword, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(JoinActivity.this);
                    queue.add(joinRequest);
                }
                else{
                    Toast.makeText(getApplicationContext(), "비밀번호가 일치하는지 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }





}
