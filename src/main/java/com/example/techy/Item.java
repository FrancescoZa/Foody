package com.example.techy;

public class Item
{

    private String nomeItem;
    private String categoriaItem;
    private String immagine;
    private String descrizioneItem;
    private String prezzoItem;
    private String itemKey;
    private String itemKeyMenu;
    private String numero;
    private String position;

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Boolean getOrdinato() {
        return ordinato;
    }

    public void setOrdinato(Boolean ordinato) {
        this.ordinato = ordinato;
    }

    Boolean ordinato;

    public String getPrezzoItem() {
        return prezzoItem;
    }

    public void setPrezzoItem(String prezzoItem) {
        this.prezzoItem = prezzoItem;
    }

    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    public String getNomeItem() {
        return nomeItem;
    }

    public void setNomeItem(String nomeItem) {
        this.nomeItem = nomeItem;
    }

    public String getCategoriaItem() {
        return categoriaItem;
    }

    public void setCategoriaItem(String categoriaItem) {
        this.categoriaItem = categoriaItem;
    }

    public String getImmagine() {
        return immagine;
    }

    public void setImmagine(String immagine) {
        this.immagine = immagine;
    }

    public String getDescrizioneItem() {
        return descrizioneItem;
    }

    public void setDescrizioneItem(String descrizioneItem) {
        this.descrizioneItem = descrizioneItem;
    }


    public Item(String nomeItem, String categoriaItem, String immagine, String descrizioneItem, String prezzoItem, String itemKey) {
        this.nomeItem = nomeItem;
        this.categoriaItem = categoriaItem;
        this.immagine = immagine;
        this.descrizioneItem = descrizioneItem;
        this.prezzoItem = prezzoItem;
        this.itemKey = itemKey;
    }

    public Item(String nomeItem, String categoriaItem, String immagine, String descrizioneItem, String prezzoItem) {
        this.nomeItem = nomeItem;
        this.categoriaItem = categoriaItem;
        this.immagine = immagine;
        this.descrizioneItem = descrizioneItem;
        this.prezzoItem = prezzoItem;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getItemKeyMenu() {
        return itemKeyMenu;
    }

    public void setItemKeyMenu(String itemKeyMenu) {
        this.itemKeyMenu = itemKeyMenu;
    }

    //itemKey = key dell'item nel carrello
    public Item(String nomeItem, String immagine, String prezzoItem, String numero, Boolean ordinato, String itemKey, String itemKeyMenu) {
        this.nomeItem = nomeItem;
        this.immagine = immagine;
        this.prezzoItem = prezzoItem;
        this.ordinato = ordinato;
        this.numero = numero;
        this.itemKey = itemKey;
        this.itemKeyMenu = itemKeyMenu;

    }

}
