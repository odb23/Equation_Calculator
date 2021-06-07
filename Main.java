package sample;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Date;
import java.util.Stack;

enum MethodType {
    // This is for selecting the method type.
    BISECTION,
    NEWTON_rALPHSON,
    SECANT
}

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Calculator pane = new Calculator();
        pane.setBackground(Background.EMPTY);
        Scene scene = new Scene(pane, 300, 650);
        scene.setFill(Color.LIGHTGRAY);

        primaryStage.setTitle("Calculator");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}


class Calculator extends VBox {
   private TextField questionField, solution, execTime, iterationCount, fieldA, fieldB;
   private MethodType selectedMethod;

    public Calculator () {
        setAlignment(Pos.CENTER);
        setPadding(new Insets(15,15,15,15));
        setSpacing(20);

        Label howLabel = new Label("How to use?");
        howLabel.setAlignment(Pos.BASELINE_RIGHT);
        howLabel.setUnderline(true);
        howLabel.setOnMouseClicked(event -> {
            Dialog<ButtonType> dialog = createDialogPane();
            dialog.initOwner(this.getScene().getWindow());
            dialog.show();
        });
        getChildren().add(howLabel);

        // add a textfield for question input
        questionField = new TextField();
        questionField.setPrefHeight(75);
        questionField.setPrefWidth(250);
        questionField.setFocusTraversable(false);
        questionField.setAlignment(Pos.BOTTOM_RIGHT);
        questionField.setFont(Font.font(null, FontWeight.BOLD, FontPosture.ITALIC, 20));
        getChildren().add(questionField);

        // add radio buttons for solution methods and add them to a toggleGroup.
        VBox radioButonPane = new VBox();
        radioButonPane.setAlignment(Pos.CENTER);
        radioButonPane.setSpacing(5);
        ToggleGroup methodToggle = new ToggleGroup();
        RadioButton rbtn1 = new RadioButton("Bisection method");
        rbtn1.setToggleGroup(methodToggle);
        rbtn1.setOnAction(event -> {
            if (rbtn1.isSelected()) {
                selectedMethod = MethodType.BISECTION;
                fieldB.setDisable(false);
            }
        });
        RadioButton rbtn2 = new RadioButton("Newton-Ralphson's method");
        rbtn2.setToggleGroup(methodToggle);
        rbtn2.setOnAction(event -> {
            if (rbtn2.isSelected()) {
                selectedMethod = MethodType.NEWTON_rALPHSON;
                fieldB.setText("");
                fieldB.setDisable(true);
            }
        });
        RadioButton rbtn3 = new RadioButton("Secant method");
        rbtn3.setToggleGroup(methodToggle);
        rbtn3.setOnAction(event -> {
            if (rbtn3.isSelected()) {
                selectedMethod = MethodType.SECANT;
                fieldB.setDisable(false);
            }
        });
        radioButonPane.getChildren().addAll(rbtn1, rbtn2, rbtn3);
        getChildren().add(radioButonPane);

        // add textfield for intervals
        VBox _intervals = new VBox();
        _intervals.setSpacing(5);
        _intervals.setAlignment(Pos.CENTER);
        HBox intervals = new HBox();
        intervals.setAlignment(Pos.CENTER);
        intervals.setSpacing(30);
        fieldA = new TextField();
        fieldA.setPrefWidth(110);
        fieldA.setPrefHeight(35);
        fieldA.setFocusTraversable(false);
        fieldA.setAlignment(Pos.CENTER);
        fieldB = new TextField();
        fieldB.setPrefWidth(110);
        fieldB.setPrefHeight(35);
        fieldB.setFocusTraversable(false);
        fieldB.setAlignment(Pos.CENTER);
        intervals.getChildren().addAll(fieldA,fieldB);
        _intervals.getChildren().addAll(new Label("Intervals / Approx. root:"), intervals);
        getChildren().add(_intervals);

        // create buttons to calculate and to clear all fields.
        Button solutionBtn = new Button("CALCULATE");
        solutionBtn.setOnAction(event -> {
            boolean isIntervalsEmpty = false;
            if (selectedMethod == MethodType.BISECTION || selectedMethod == MethodType.SECANT) {
                if (fieldA.getText().isEmpty() || fieldB.getText().isEmpty()) {
                    isIntervalsEmpty = true;
                }
            }else if (selectedMethod == MethodType.NEWTON_rALPHSON) {
                if (fieldA.getText().isEmpty()) {
                    isIntervalsEmpty = true;
                }
            }
            if (selectedMethod != null && (!questionField.getText().isEmpty()) && !isIntervalsEmpty) {
                getSolution(this.questionField.getText(), fieldA.getText(), fieldB.getText());
            }
        } );
        Button clearBtn = new Button("CLEAR ALL");
        clearBtn.setOnAction(event -> clearAll());
        HBox buttonPane = new HBox();
        buttonPane.setAlignment(Pos.CENTER);
        buttonPane.setSpacing(20);
        buttonPane.getChildren().addAll(solutionBtn, clearBtn);
        getChildren().add(buttonPane);

        // creating textfield to show results, execution time, and number of iterations.
        solution = createResultField();
        VBox solutionPane = new VBox();
        solutionPane.setAlignment(Pos.CENTER);
        solutionPane.setSpacing(5);
        solutionPane.getChildren().addAll(new Label("Root of equation:"),solution);

        execTime = createResultField();
        VBox execPane = new VBox();
        execPane.setAlignment(Pos.CENTER);
        execPane.setSpacing(5);
        execPane.getChildren().addAll( new Label("Execution time:"), execTime);

        iterationCount = createResultField();
        VBox iterPane = new VBox();
        iterPane.setAlignment(Pos.CENTER);
        iterPane.setSpacing(5);
        iterPane.getChildren().addAll(new Label("Number of iterations:"), iterationCount);

        getChildren().addAll(solutionPane, execPane, iterPane);
    }

    private Dialog<ButtonType> createDialogPane () {
        Dialog<ButtonType> dialog = new Dialog<>();
        DialogPane dialogPane = new DialogPane();
        dialog.setTitle("Instructions");
        dialog.setResizable(false);
        dialog.setDialogPane(dialogPane);
        TextArea instructionsArea = new TextArea("1. Equation should be written in the format ax^n + bx^m + c.\n" +
                "2. Operators should be separated from each terms with spaces.\n" +
                "NOTE: Incorrect equation could lead to either a wrong answer or no answer. Kindly check if equation follows" +
                " the format above. Thanks.");
        instructionsArea.setWrapText(true);
        instructionsArea.setEditable(false);
        dialog.getDialogPane().setContent(new ScrollPane(instructionsArea));


        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        return dialog;
    }

    public void getSolution (String question, String a, String b) {
        if (getMethodType() == MethodType.BISECTION) {  // solve the equation using bisection method

            Solution answer = Controller.bisectionSolution(question, a, b);
            solution.setText(answer.getSolution());
            iterationCount.setText(answer.getIterationCount());
            execTime.setText(answer.getExecTime());

        }else if (getMethodType() == MethodType.NEWTON_rALPHSON) { // get solution using newton ralphson

            Solution answer = Controller.newtonRalphsonSolution(question, a);
            solution.setText(answer.getSolution());
            iterationCount.setText(answer.getIterationCount());
            execTime.setText(answer.getExecTime());

        }else if (getMethodType() == MethodType.SECANT) {   //gets solution using secant method

            Solution answer = Controller.secantSolution(question, a, b);
            solution.setText(answer.getSolution());
            iterationCount.setText(answer.getIterationCount());
            execTime.setText(answer.getExecTime());

        }
    }

    public void clearAll () {
        questionField.setText("");
        solution.setText("");
        execTime.setText("");
        iterationCount.setText("");
    }

    public MethodType getMethodType () {
        return this.selectedMethod;
    }

    private TextField createResultField () {
        TextField result = new TextField();
        result.setEditable(false);
        result.setPrefWidth(10);
        result.setFocusTraversable(false);
        result.setAlignment(Pos.CENTER);
        result.setFont(Font.font(null, FontWeight.NORMAL, FontPosture.REGULAR, 18));
        return result;
    }
}


class Solution {

    private String solution;
    private String iterationCount;
    private  String execTime;

    public  Solution (String solution, String iter, String exec) {
        this.solution = solution;
        this.iterationCount = iter;
        this.execTime = exec;
    }

    public String getSolution() {
        return solution;
    }

    public String getIterationCount() {
        return iterationCount;
    }

    public String getExecTime() {
        return execTime;
    }
}


final class Controller {
    private Controller() {}

    public static Solution secantSolution (String func, String aStr, String bStr) {
        double x,x1,x2;
        String err;
        int iterCount = 0;
        long init, end;

        try{
            x1 = Double.parseDouble(aStr);
            x2 = Double.parseDouble(bStr);

            if (f(func,x1,false) * f(func,x2,false) > 0) {
                return new Solution("Root not in intervals.", "", "");
            }else{
                init = new Date().getTime();
                do {
                    x = ((x1 * f(func, x2, false)) -
                            (x2 * f(func, x1, false))) /
                            (f(func, x2, false) - f(func, x1, false));

                    System.out.println("calculating, x is " + x);
                    x1 = x2;
                    x2 = x;
                    iterCount++;
                } while (f(func, x1, false) * f(func, x, false) != 0);
                end = new Date().getTime();

                return new Solution(String.format("%.6f", x), iterCount + " iterations.", (end - init) + " ms");
            }
        }catch (NumberFormatException e) {
            err = "Invalid intervals";
        }
        return new Solution(err, "", "");
    }

    public static Solution newtonRalphsonSolution(String func, String xStr) {
        String err;
        int iterCount = 0;
        long initTime, endTime;
        try{
            double x = Double.parseDouble(xStr);
            double h;
            initTime = new Date().getTime();
            while(true) {
                iterCount++;
                double fX = f(func, x, false);
                if (fX == 0) break;

                h = fX / f(func, x, true);
                x = x- h;
            }
            endTime = new Date().getTime();
            return new Solution(String.format("%.6f", x), iterCount + " iterations",
                    (endTime - initTime) + " ms");
        }catch (NumberFormatException e ) {
            err = "Invalid approx. root";
        }
        return new Solution(err, "", "");
    }

    public static Solution bisectionSolution(String func, String aStr , String bStr) {
        String err;
        int iterNum = 0;
        long initTime, endTime;

        try{
            double a = Double.parseDouble(aStr);
            double b = Double.parseDouble(bStr);

            if (f(func,a ,false) * f(func, b, false) >= 0) {
                return new Solution("Root not in given intervals.", "","");
            }else{
                double c;
                initTime = new Date().getTime();

                while (true) {
                    c = (a + b)/2;  //find midpoint
                    iterNum++;
                    System.out.println(f(func, c, false));
                    if (f(func, c, false) == 0.0) {   // check if midpoint is the root
                        break;
                    }else if (f(func, c, false) * f(func, a ,false) < 0) {
                        b = c;
                    }else {
                        a = c;
                    }
                }
                endTime = (new Date().getTime()) - initTime;
                return new Solution(String.format("%.6f", c), iterNum + " iterations",
                        endTime + " ms");
            }
        }catch (NumberFormatException e) {
            err = "Invalid intervals";
        }
        return  new Solution(err, "", "");
    }

    private static double f(String a, double val, boolean differentiate) { //-38x^4
        if (a.charAt(0) != '-') {
            a = "+ " + a;
        }else {
            a = "- " + a.substring(1);
        }
        double ans = 0;
        String[] _expression = a.split(" ");
        Stack<String> expression = new Stack<>();
        Stack<Character> op = new Stack<>();
        for (String s: _expression) {
            if (s.equals("+") || s.equals("-")) {
                op.push(s.trim().charAt(0));
            }else{
                expression.push(s);
            }

        }

        while (!expression.isEmpty() && !op.isEmpty()) {
            if (op.peek() == '+') {
                ans += calculateTerm(expression.pop(), val, differentiate);
                op.pop();
            }else if (op.peek() == '-') {
                ans -= calculateTerm(expression.pop(), val, differentiate);
                op.pop();
            }
        }
        return Double.parseDouble(String.format("%.4f", ans));
    }

    private static double calculateTerm (String term, double val, boolean differentiate) {
        if (!term.contains("x")) {
            if (!differentiate) {
                return Double.parseDouble(term);
            }
            return 0;
        }  // if its a constant return it.

        // get co-efficient from the term
        StringBuilder coeffStr = new StringBuilder();
        int i ;
        for (i = 0; term.charAt(i) != 'x'; i++) {
            if (term.charAt(i) == ' ') continue;
            coeffStr.append(term.charAt(i));
        }
        double coeff = (coeffStr.toString().equals("")) ? 1 : Double.parseDouble(coeffStr.toString());

        // Get power from the String.
        if (!term.contains("^")) {
            if (!differentiate) {
                return (coeff * val);
            }else {
                return coeff;
            }
        }
        StringBuilder powStr = new StringBuilder();
        for (i += 2; i != term.length() && term.charAt(i) != ' '; i++) { // to skip characters (x and ^).
            powStr.append(term.charAt(i));
        }
        double power = Double.parseDouble(powStr.toString());
        if (!differentiate) {
            return (coeff * Math.pow(val,power));
        }else {
            return coeff * power * Math.pow(val,power-1);
        }
    }
}