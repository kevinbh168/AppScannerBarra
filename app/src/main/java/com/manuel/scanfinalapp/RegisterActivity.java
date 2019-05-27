package com.manuel.scanfinalapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.manuel.scanfinalapp.models.Producto;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private EditText etcodigo,etgrupo,etmarca,etmodelo,etserie;
    private Button btnguardar, btnconsultar;
    private ListView listaTodo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etcodigo=findViewById(R.id.etCodigo);
        etgrupo=findViewById(R.id.etGrupo);
        etmarca=findViewById(R.id.etMarca);
        etmodelo=findViewById(R.id.etModelo);
        etserie=findViewById(R.id.etSerie);

        listaTodo=findViewById(R.id.LVMostrar);

        btnguardar=findViewById(R.id.btnGuardar);
        btnconsultar=findViewById(R.id.btnConsultar);

        btnguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        btnconsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarList();
            }
        });
    }
    private void save(){

        try {
            String codigo=etcodigo.getText().toString();
            String grupo=etgrupo.getText().toString();
            String marca=etmarca.getText().toString();
            String modelo=etmodelo.getText().toString();
            String serie=etserie.getText().toString();

            Producto producto=new Producto();
            producto.setCodigo(codigo);
            producto.setGrupo(grupo);
            producto.setMarca(marca);
            producto.setModelo(modelo);
            producto.setSerie(serie);
            SugarRecord.save(producto);

            Toast.makeText(this, "Registro satisfactorio", Toast.LENGTH_SHORT).show();

            finish();
        }catch (Exception e){
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    public void cargarList(){
        List<Producto> productos = Producto.listAll(Producto.class);
        ArrayList<String> lista1=new ArrayList<String>();
        for(int i=0;i<productos.size();i++){
            Producto prod=productos.get(i);
            lista1.add("Codigo: "+prod.getCodigo()+"\nGrupo: "+prod.getGrupo()+"\nMarca: "+prod.getMarca()+"\nModelo: "+prod.getModelo()+"\nSerie: "+prod.getSerie());

        }
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,lista1);
        listaTodo.setAdapter(adapter);
    }
}
