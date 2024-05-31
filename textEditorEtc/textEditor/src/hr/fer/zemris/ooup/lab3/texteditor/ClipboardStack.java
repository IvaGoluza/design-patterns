package hr.fer.zemris.ooup.lab3.texteditor;

import hr.fer.zemris.ooup.lab3.texteditor.observer.ClipboardObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ClipboardStack {

    private final Stack<String> texts;
    private final List<ClipboardObserver> clipboardObservers;

    public ClipboardStack() {
        this.texts = new Stack<>();
        this.clipboardObservers = new ArrayList<>();
    }

    public void subscribeClipboardObserver(ClipboardObserver clipboardObserver) {    // TKO SE TREBA PRETPLATITIIII
        this.clipboardObservers.add(clipboardObserver);
    }

    public void unsubscribeClipboardObserver(ClipboardObserver clipboardObserver) {
        this.clipboardObservers.remove(clipboardObserver);
    }

    public void notifyClipboardObservers() {
        this.clipboardObservers.forEach(observer -> observer.updateClipboard(this));
    }

    public void push(String text) {
        if(!isEmpty() && peek().equals(text)) return;
        this.texts.push(text);
        notifyClipboardObservers();
    }

    public String pop() {
        if(isEmpty()) return null;
        String res = texts.pop();
        notifyClipboardObservers();
        return res;
    }

    public String peek() {
        return this.texts.peek();
    }

    public boolean isEmpty() {
        return this.texts.isEmpty();
    }

    public void clear() {
        this.texts.clear();
        notifyClipboardObservers();
    }

}
