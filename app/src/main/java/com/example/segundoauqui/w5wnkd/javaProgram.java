package com.example.segundoauqui.w5wnkd;

import java.util.Stack;

/**
 * Created by segundoauqui on 9/6/17.
 */

public class javaProgram {

    public static void main(String[] args) {
        String str = "({})[]";
        System.out.println(matchingString(str));
        System.out.println("First String = " + String.valueOf(strCopies("catcowcat", "cat", 2))
                + "\nSecond String = " + String.valueOf(strCopies("catcowcat", "cow", 2))
                + "\nThird String = " + String.valueOf(strCopies("catcowcat", "cow", 1)));
    }

    public static boolean matchingString(String str) {
        if (str.charAt(0) != '(')
            return false;
        Stack<Character> stack = new Stack<Character>();
        char c;
        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            if (c == '(')
                stack.push(c);
            else if (c == '{')
                stack.push(c);
            else if (c == '[')
                if (stack.empty())
                    return false;
                else if (stack.peek() == ')')
                    stack.pop();
                else
                    stack.push(c);
            else if (c == ')')
                if (stack.empty())
                    return false;
                else
                    stack.push(c);
            else if (c == '}')
                if (stack.empty())
                    return false;
                else if (stack.peek() == '{')
                    stack.pop();
                else
                    stack.push(c);
            else if (c == ']')
                if (stack.empty())
                    return false;
                else if (stack.peek() == '(')
                    stack.pop();
                else
                    return false;
        }
        return stack.empty();
    }
    public static boolean strCopies(String str, String sub, int n) {
        if (n == 0)
            return true;
        if (str.length() < sub.length())
            return false;

        if (str.substring(0, sub.length()).equals(sub))
            return strCopies(str.substring(1), sub, n - 1);

        return strCopies(str.substring(1), sub, n);
    }

}
