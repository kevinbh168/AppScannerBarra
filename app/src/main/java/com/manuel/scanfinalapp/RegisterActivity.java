package com.manuel.scanfinalapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.manuel.scanfinalapp.models.Producto;
import com.orm.SugarRecord;
import com.manuel.scanfinalapp.R;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class RegisterActivity extends AppCompatActivity {

    private EditText etcodigo,etgrupo,etmarca,etmodelo,etserie,etCantidad;
    private RelativeLayout rlCambios,rlRegistoDetalle;
    private Button btnguardar, btnconsultar,btnRealizarCambios;
    private ListView listaTodo;
    private TextView tvtitulo;
    private Spinner tipoMovimiento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etcodigo=findViewById(R.id.etCodigo);
        etgrupo=findViewById(R.id.etGrupo);
        etmarca=findViewById(R.id.etMarca);
        etserie=findViewById(R.id.etSerie);
        etCantidad=findViewById(R.id.etcantidad);
        tipoMovimiento=findViewById(R.id.spinnerMovimiento);
        tvtitulo=findViewById(R.id.tvtitulo);
        btnguardar=findViewById(R.id.btnGuardar);
        btnconsultar=findViewById(R.id.btnConsultar);
        btnRealizarCambios=findViewById(R.id.btnCambios);
        listaTodo=findViewById(R.id.LVMostrar);
        rlCambios=findViewById(R.id.rlCambios);
        rlRegistoDetalle=findViewById(R.id.rlRegistroDetalle);
        btnRealizarCambios=findViewById(R.id.btnCambios);

        final String codigo=getIntent().getStringExtra("codigo");
        final String titulo=getIntent().getStringExtra("titulo");
        String actividad=getIntent().getStringExtra("actividad");

        if(actividad.equals("insertar")){
            tvtitulo.setText(titulo);
            if(!codigo.isEmpty()){
                etcodigo.setText(codigo);
            }
            btnguardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    save();
                }
            });
        }
        if(actividad.equals("verDetalle")){
            tvtitulo.setText(titulo);
            SugarRecord.find(Producto.class,"codigo=?",codigo);

            List<Producto> productos=SugarRecord.find(Producto.class,"codigo=?",codigo);

            if(productos.size()>0) {
                Producto prod = productos.get(0);
                etcodigo.setText(prod.getCodigo());
                etgrupo.setText(prod.getCategoria());
                etmarca.setText(prod.getDescripcion());
                etserie.setText(String.valueOf(prod.getStock()));
            }

                etcodigo.setFocusable(false);
                etgrupo.setFocusable(false);
                etmarca.setFocusable(false);
                etmarca.setFocusable(false);
                etserie.setFocusable(false);
                btnconsultar.setVisibility(View.GONE);
                btnguardar.setText("Realizar cambios");


                btnguardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(RegisterActivity.this,RegisterActivity.class);
                    intent.putExtra("codigo",codigo);
                    intent.putExtra("titulo", "Movimiento de stock");
                    intent.putExtra("actividad","modificarStock");
                    startActivity(intent);
                }
            });

        }
        if(actividad.equals("modificarStock")){
            tvtitulo.setText(titulo);
            rlRegistoDetalle.setVisibility(View.GONE);
            rlCambios.setVisibility(View.VISIBLE);


            SugarRecord.find(Producto.class,"codigo=?",codigo);
            List<Producto> productos=SugarRecord.find(Producto.class,"codigo=?",codigo);
            final Producto prod=productos.get(0);


            btnRealizarCambios.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    actualizar(prod.getId());
                }
            });
        }

        btnconsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarList();
            }
        });
    }

    private void actualizar(Long codigo){
        String textoCantidad=etCantidad.getText().toString();
        if(tipoMovimiento.getSelectedItemPosition()!=0 && !textoCantidad.isEmpty() ){
            int cantidad=Integer.parseInt(textoCantidad);
            int movimiento=tipoMovimiento.getSelectedItemPosition();
            Producto producto=SugarRecord.findById(Producto.class,codigo);
            if(movimiento==1) {
                if(cantidad>0){
                    producto.setStock(producto.getStock() + cantidad);
                    producto.save();

                }else{
                    Toasty.error(this,"Ingrese valor valido",Toasty.LENGTH_SHORT).show();
                }
            }if(movimiento==2){
                if(cantidad>0) {
                    if(producto.getStock()>cantidad) {
                        producto.setStock(producto.getStock() - cantidad);
                        producto.save();
                    }else{
                        Toasty.error(this,"El stock es menor al numero ingresado",Toasty.LENGTH_SHORT).show();
                    }
                }else{
                    Toasty.error(this,"Ingrese valor valido",Toasty.LENGTH_SHORT).show();
                }
            }
            Toasty.success(this,"Cambios realizados correctamente",Toasty.LENGTH_SHORT).show();
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);
        }else{
            Toasty.warning(this,"Debe seleccionar el tipo de movimiento o ingresar valor",Toasty.LENGTH_SHORT).show();
        }



    }

    private void save(){

            String codigo=etcodigo.getText().toString();
            String categoria=etgrupo.getText().toString();
            String descripcion=etmarca.getText().toString();
            String cantidad=etserie.getText().toString();


            if(codigo.isEmpty() || categoria.isEmpty() || descripcion.isEmpty() || cantidad.isEmpty()){

                Toasty.error(this, "Debe completar el formulario", Toast.LENGTH_SHORT, true).show();
                return;
            }else{
                int stock=Integer.parseInt(cantidad);
                Producto producto=new Producto();
                producto.setCodigo(codigo);
                producto.setCategoria(categoria);
                producto.setDescripcion(descripcion);
                producto.setStock(stock);

                SugarRecord.save(producto);
                Toasty.success(this, "El producto se registro correctamente", Toast.LENGTH_SHORT).show();
                finish();
            }


    }

    public void cargarList(){
        List<Producto> productos = Producto.listAll(Producto.class);
        if(productos.size()>0) {
            ArrayList<String> lista1 = new ArrayList<String>();
            for (int i = 0; i < productos.size(); i++) {
                Producto prod = productos.get(i);
                lista1.add("Codigo: " + prod.getCodigo() + "\nCategoria: " + prod.getCategoria() + "\nDescripcion: " + prod.getDescripcion() + "\nStock: " + prod.getStock());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lista1);
            listaTodo.setAdapter(adapter);
        }else{
            Toasty.info(this,"No hay productos",Toasty.LENGTH_SHORT).show();
        }
    }
}
