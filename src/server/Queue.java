package server;

 

/**
 * Die generische Klasse Queue<ContentType>
 * Objekte der generischen Klasse Queue (Warteschlange) verwalten beliebige 
 * Objekte vom Typ ContentType nach dem First-In-First-Out-Prinzip, d.h., 
 * das zuerst abgelegte Objekt wird als erstes wieder entnommen. 
 * Alle Methoden haben eine konstante Laufzeit, unabhaengig von der Anzahl 
 * der verwalteten Objekte.
 *
 * @version 2015-06-22
 */

public class Queue<ContentType> {
  
  // private Klasse fuer ein Element der Schlange
  private class Node {
    private ContentType content = null;
    private Node nextNode = null;
    
    public Node() {
      nextNode = null;
    }
    
    public Node(ContentType pContent) {
      content = pContent;
      nextNode = null;
    }
    
    public void setNext(Node pNext) {
      nextNode = pNext;
    }
    
    public Node getNext() {
      return nextNode;
    }
    
    public ContentType getContent() {
      return content;
    }
    
    public void setContent(ContentType pContent) {
      content = pContent;
    }
    
  } // Ende private Klasse fuer ein Element
  
  // Attribute der Klasse Queue
  private Node head;
  private Node tail;
  
  // Methoden der Klasse Queue
  
  /**
  * Eine leere Schlange wird erzeugt. Objekte, die in dieser Schlange 
  * verwaltet werden, muessen vom Typ ContentType sein.
  */
  public Queue() {
    head = null;
    tail = null;
  }
  
  /**
  * Die Anfrage liefert den Wert true, wenn die Schlange keine Objekte 
  * enthaelt, sonst liefert sie den Wert false.
  *
  * @return true, falls die Schlange leer ist, sonst false
  */
  public boolean isEmpty() {
    return head == null;
  }
  
  /**
  * Das Objekt pContent wird an die Schlange angehaengt. Falls 
  * pContent gleich null ist, bleibt die Schlange unveraendert.
  * 
  * @param pContent das anzuhaengende Objekt
  */
  public void enqueue(ContentType pContent) { 
    if (pContent != null){
      Node newNode = new Node();
      newNode.setContent(pContent);
      if (this.isEmpty()) {
        head = newNode;
        tail = newNode;
      } else {
        tail.setNext(newNode);
        tail = newNode;
      }
    }  
  }
  
  /**
  * Das erste Objekt wird aus der Schlange entfernt. Falls die Schlange 
  * leer ist, wird sie nicht veraendert. 
  */
  public void dequeue() {
    if (!this.isEmpty()){
      head = head.getNext();
    }
  }
  
  /**
  * Die Anfrage liefert das erste Objekt der Schlange. Die Schlange 
  * bleibt unveraendert. Falls die Schlange leer ist, wird null zurueckgegeben.
  * 
  * @return das erste Objekt der Schlange oder null, falls 
  * die Schlange leer ist.
  */
  public ContentType front() {
    if (this.isEmpty()) {
      return null;
    } else {
      return head.getContent();
    }
  }
  
}

