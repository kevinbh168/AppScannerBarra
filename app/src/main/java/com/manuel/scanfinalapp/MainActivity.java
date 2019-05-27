package com.manuel.scanfinalapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.zxing.Result;
import com.manuel.scanfinalapp.models.Producto;
import com.orm.SugarRecord;

import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private Button btnRegister;
    private ZXingScannerView mScannerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.Title_scan);

        //Abrir ventana para registrar
        btnRegister=findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }
    public void openScanner(View v){
        mScannerView=new ZXingScannerView(this);
        setContentView(mScannerView);
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void handleResult(Result rawResult) {

        final String text=rawResult.getText();
        Log.v("HandleResult",text);

        SugarRecord.find(Producto.class,"codigo=?",text);

        List<Producto> productos=SugarRecord.findWithQuery(Producto.class,"Select * from Producto where codigo=?", text);

        if(productos.size()>0){
            Producto prod=productos.get(0);

            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("Resultado del escaneo");

            builder.setMessage("Codigo: "+prod.getCodigo()+"\nGrupo: "+prod.getGrupo()+"\nMarca: "+prod.getMarca()+"\nModelo: "+prod.getModelo()+"\nSerie: "+prod.getSerie());
            AlertDialog alertDialog=builder.create();
            alertDialog.show();
        }else{
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("Error");

            builder.setMessage("Producto no existente, ¿Desea registrar el producto?");
            builder.setCancelable(false);
            builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    aceptar();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancelar();
                }
            });
            AlertDialog alertDialog=builder.create();
            alertDialog.show();
        }
        mScannerView.resumeCameraPreview(this);

    }

    public void aceptar(){
        Intent intent=new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void cancelar(){
        finish();
    }
}
