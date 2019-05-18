package com.example.andrey.crediexpressgolden;
class SubjectData {
    String SubjectName;
    String Documento;
    String Direccion;
    String Telefono;
    String Seguro;
    String Image;
    String Codeud;
    String DClien;
    String Coordenadas;

    public SubjectData(String subjectName, String Documento, String Direccion, String Telefono, String Seguro, String image, String Codeud, String DClien, String Coordenadas) {
        this.SubjectName = subjectName;
        this.Documento = Documento;
        this.Direccion = Direccion;
        this.Telefono = Telefono;
        this.Seguro = Seguro;
        this.Image = image;
        this.Codeud = Codeud;
        this.DClien = DClien;
        this.Coordenadas = Coordenadas;

    }

    public String getSubjectName() {
        return SubjectName;
    }

    public String getDocumento() {
        return Documento;
    }

    public String getDireccion() {
        return Direccion;
    }

    public String getTelefono() {
        return Telefono;
    }

    public String getSeguro() {
        return Seguro;
    }

    public String getImage() {
        return Image;
    }

    public String getCodeud() {
        return Codeud;
    }

    public String getDClien() {
        return DClien;
    }
}