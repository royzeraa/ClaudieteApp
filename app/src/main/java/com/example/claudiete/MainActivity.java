
package com.example.claudiete;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText txtNombre, txtApellido, txtEdad;
    private Button btnGuardar;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Conectar con Firebase Realtime Database
        mDatabase = FirebaseDatabase
                .getInstance("https://appclaudiete-default-rtdb.firebaseio.com/")
                .getReference("usuarios");

        // Vincular los componentes del XML
        txtNombre = findViewById(R.id.nombre);
        txtApellido = findViewById(R.id.apellido);
        txtEdad = findViewById(R.id.edad);
        btnGuardar = findViewById(R.id.btnGuardar);

        // Acción del botón Guardar
        btnGuardar.setOnClickListener(v -> guardarUsuario());
    }

    private void guardarUsuario() {

        String nombre = txtNombre.getText().toString().trim();
        String apellido = txtApellido.getText().toString().trim();
        String edadTexto = txtEdad.getText().toString().trim();

        // Validar campos vacíos
        if (nombre.isEmpty() || apellido.isEmpty() || edadTexto.isEmpty()) {
            Toast.makeText(
                    this,
                    "Complete todos los campos",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // Validar edad
        int edad;

        try {
            edad = Integer.parseInt(edadTexto);

            if (edad <= 0 || edad > 120) {
                Toast.makeText(
                        this,
                        "Ingrese una edad válida",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

        } catch (NumberFormatException e) {
            Toast.makeText(
                    this,
                    "La edad debe ser un número",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // Generar identificador único
        String userId = mDatabase.push().getKey();

        if (userId == null) {
            Toast.makeText(
                    this,
                    "No se pudo generar el identificador",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // Crear objeto con los datos del usuario
        Map<String, Object> usuario = new HashMap<>();

        usuario.put("nombre", nombre);
        usuario.put("apellido", apellido);
        usuario.put("edad", edad);

        // Guardar información en Firebase
        mDatabase.child(userId).setValue(usuario)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            MainActivity.this,
                            "Datos guardados correctamente",
                            Toast.LENGTH_SHORT
                    ).show();

                    // Limpiar campos después de guardar
                    txtNombre.setText("");
                    txtApellido.setText("");
                    txtEdad.setText("");

                })
                .addOnFailureListener(error -> {

                    Toast.makeText(
                            MainActivity.this,
                            "Error al guardar: " + error.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });
    }
}
