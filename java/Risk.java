package com.example.horapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ImageView;

import java.util.Locale;

public class Risk extends AppCompatActivity implements View.OnClickListener{

    TextView totalQuestionsTextView;
    TextView questionTextView;
    TextView ansA, ansB, ansC, ansD;
    TextView submitBtn;
    ProgressBar progressBar;
    TextView tvProgress;
    ImageView questionImageView;

    int score=0;
    int totalQuestion = Question.question.length;
    int currentQuestionIndex = 0;
    String selectedAnswer = "";
    int maxProgress = Question.question.length;

    int currentImageResource = 0;

    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_risk);

        totalQuestionsTextView = findViewById(R.id.total_question);
        questionTextView = findViewById(R.id.question);
        ansA = findViewById(R.id.ans_A);
        ansB = findViewById(R.id.ans_B);
        ansC = findViewById(R.id.ans_C);
        ansD = findViewById(R.id.ans_D);
        submitBtn = findViewById(R.id.submit_btn);
        progressBar = findViewById(R.id.progressBar);
        tvProgress = findViewById(R.id.tv_progress);

        updateProgressText();

        ansA.setOnClickListener(this);
        ansB.setOnClickListener(this);
        ansC.setOnClickListener(this);
        ansD.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

        totalQuestionsTextView.setText("Total questions : "+totalQuestion);

        loadNewQuestion();

    }

    private void updateProgressText() {
        int progress = progressBar.getProgress();
        tvProgress.setText(String.format(Locale.getDefault(), "%d/%d", progress, maxProgress));
    }

    @Override
    public void onClick(View view) {

        ansA.setBackgroundColor(Color.WHITE);
        ansB.setBackgroundColor(Color.WHITE);
        ansC.setBackgroundColor(Color.WHITE);
        ansD.setBackgroundColor(Color.WHITE);

        TextView clickedTextView = (TextView) view;
        if(clickedTextView.getId()==R.id.submit_btn){
            if(selectedAnswer.equals(Question.choices[currentQuestionIndex][0])){
                // great
            }else if (selectedAnswer.equals(Question.choices[currentQuestionIndex][1])) {
                score ++;
            } else if (selectedAnswer.equals(Question.choices[currentQuestionIndex][2])) {
                score += 2;
            } else if (selectedAnswer.equals(Question.choices[currentQuestionIndex][3])) {
                score += 3;
            }
            currentQuestionIndex++;
            loadNewQuestion();
            incrementProgressBar();

        }else{
            selectedAnswer  = clickedTextView.getText().toString();
            if (clickedTextView.getId() == R.id.ans_A) {
                clickedTextView.setBackgroundColor(getResources().getColor(R.color.light_blue));
            } else if (clickedTextView.getId() == R.id.ans_B) {
                clickedTextView.setBackgroundColor(getResources().getColor(R.color.light_blue));
            } else if (clickedTextView.getId() == R.id.ans_C) {
                clickedTextView.setBackgroundColor(getResources().getColor(R.color.light_blue));
            } else if (clickedTextView.getId() == R.id.ans_D) {
                clickedTextView.setBackgroundColor(getResources().getColor(R.color.light_blue));
            }
            //choices button clicked

        }

    }

    private void incrementProgressBar() {
        int progress = progressBar.getProgress() + 1;
        progressBar.setProgress(progress);
        updateProgressText();
    }

    void loadNewQuestion(){

        if(currentQuestionIndex == totalQuestion ){
            finishQuiz();
            return;
        }
        // Inside loadNewQuestion method in your MainActivity
        currentImageResource = Question.images[currentQuestionIndex];
        questionImageView = findViewById(R.id.iv_image);
        questionImageView.setImageResource(currentImageResource);

        questionTextView.setText(Question.question[currentQuestionIndex]);
        ansA.setText(Question.choices[currentQuestionIndex][0]);
        ansB.setText(Question.choices[currentQuestionIndex][1]);
        ansC.setText(Question.choices[currentQuestionIndex][2]);
        ansD.setText(Question.choices[currentQuestionIndex][3]);

    }

    void finishQuiz(){
        mediaPlayer = MediaPlayer.create(this, R.raw.securitysound);
        String passStatus = "";
        int dialogColor;

        if(score < 5){
            passStatus = "Safe";
            dialogColor = Color.LTGRAY;
        } else if (score < 15) {
            passStatus = "Unsafe";
            dialogColor = Color.GRAY;
        } else if (score < 25) {
            passStatus = "Alert";
            dialogColor = Color.DKGRAY;
        } else{
            passStatus = "Dangerous";
            dialogColor = Color.RED;
            mediaPlayer.start();
        }
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(passStatus)
                .setMessage("Score is "+ score+" out of "+ totalQuestion + " questions")
                .setPositiveButton("Restart",(dialogInterface, i) -> restartQuiz() )
                .setCancelable(false)
                .show();
        // Setting the preset color on the dialog window
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(dialogColor));
    }

    void restartQuiz(){
        if(mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        score = 0;
        currentQuestionIndex = 0;
        progressBar.setProgress(0); // Reset progress bar
        updateProgressText(); // Update progress text
        loadNewQuestion();
    }

}
