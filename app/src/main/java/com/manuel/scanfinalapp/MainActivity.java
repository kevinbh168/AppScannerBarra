package com.manuel.scanfinalapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.Result;
import com.manuel.scanfinalapp.models.Producto;
import com.orm.SugarRecord;
import com.manuel.scanfinalapp.R;

import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private Button btnRegister,btnScan;
    private ZXingScannerView mScannerView;
    private int contador;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Abrir ventana para registrar
        btnRegister=findViewById(R.id.btn_register);
        btnScan=findViewById(R.id.btn_scan);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entrarFormulario("","Registrar nuevo producto","insertar");
            }
        });
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openScanner();
            }
        });

        checkAllPermissions();
    }

    @Override
    public void handleResult(Result rawResult) {


        final String codigo=rawResult.getText();
        List<Producto> productos=SugarRecord.find(Producto.class,"codigo=?",codigo);

        if(productos.size()>0){
            Producto prod=productos.get(0);

            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("RESULTADO");

            builder.setMessage("Producto encontrado,ver detalles?");
            builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    entrarFormulario(codigo,"Detalles del producto","verDetalle");
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            AlertDialog alertDialog=builder.create();
            alertDialog.show();
        }else{
            AlertDialog.Builder builder=new AlertDialog.Builder(this);

            builder.setTitle("ERROR");
            builder.setMessage("Producto no existente, ¿Desea registrar el producto?");
            builder.setCancelable(false);
            builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    entrarFormulario(codigo,"Registrar nuevo producto","insertar");
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });

            AlertDialog alertDialog=builder.create();
            alertDialog.show();
        }
        mScannerView.resumeCameraPreview(this);

    }

    public void entrarFormulario(String codigo, String titulo,String actividad){
        Intent intent=new Intent(MainActivity.this, RegisterActivity.class);
        intent.putExtra("codigo",codigo);
        intent.putExtra("titulo",titulo);
        intent.putExtra("actividad",actividad);
        startActivity(intent);
    }



    private static final int PERMISSIONS_REQUEST = 100;
    private static String[] PERMISSIONS_LIST = new String[]{Manifest.permission.CAMERA
    };

    private void checkAllPermissions() {
        boolean permissionRequired = false;
        for (String permission : PERMISSIONS_LIST) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                permissionRequired = true;
        }
        if (permissionRequired) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_LIST, PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, PERMISSIONS_LIST[i] + " permissions declined!", Toast.LENGTH_LONG).show();
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                finish();
                            }
                        }, Toast.LENGTH_LONG);
                    }
                }
            }
        }
    }

    public void openScanner(){
        setTitle(R.string.Title_scan);
        mScannerView=new ZXingScannerView(this);
        setContentView(mScannerView);
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
        contador=1;

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && contador==1) {
            startActivity(new Intent(MainActivity.this,MainActivity.class));
            contador=0;
        }else{
            finish();
        }

        return true;
    }
}
