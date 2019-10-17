package frsf.isi.dam.obrapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import frsf.isi.dam.obrapp.db.DBClient;
import frsf.isi.dam.obrapp.db.ObraAppDB;
import frsf.isi.dam.obrapp.db.ObraDao;
import frsf.isi.dam.obrapp.modelo.Obra;

public class ObraListActivity extends AppCompatActivity {

    ArrayAdapter<Obra> adapter;
    ListView lvObras;
    Button btnMenu;
    Button btnAdd;
    Button btnEditarObra;
    Button btnBorrarObra;
    List<Obra> listaObrasDataset;
    Obra obraSeleccionada;
    TextView tvObraSelec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obra_list);
        tvObraSelec = (TextView) findViewById(R.id.tvObraSeleccionada);
        lvObras = (ListView) findViewById(R.id.listaObras);
        lvObras.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        lvObras.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                obraSeleccionada = adapter.getItem(position);
                btnBorrarObra.setEnabled(true);
                btnEditarObra.setEnabled(true);
                tvObraSelec.setText("SELECCIONO LA OBRA "+ obraSeleccionada.getId()+ ":"+ obraSeleccionada.getDescripcion());
            }
        });
        btnMenu = (Button) findViewById(R.id.btnObraMenuPpal);
        btnAdd = (Button) findViewById(R.id.btnAddObra);
        btnEditarObra = (Button) findViewById(R.id.btnEditarObra);
        btnBorrarObra= (Button) findViewById(R.id.btnBorrarObra);
        btnBorrarObra.setEnabled(false);
        btnEditarObra.setEnabled(false);
        btnBorrarObra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BorrarObraAsyncTask tarea = new BorrarObraAsyncTask();
                tarea.execute(obraSeleccionada);
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ObraListActivity.this,ObraActivity.class);
                startActivity(i);            }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ObraListActivity.this,MainActivity.class);
                startActivity(i);            }
        });
        btnEditarObra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ObraListActivity.this,ObraActivity.class);
                i.putExtra("obraEditar",obraSeleccionada);
                startActivity(i);
            }
        });

        final Runnable hiloUpdateLista = new Runnable() {
            @Override
            public void run() {
                adapter = new ArrayAdapter<>(ObraListActivity.this,android.R.layout.simple_list_item_single_choice,listaObrasDataset);
                lvObras.setAdapter(adapter);
            }
        };
        final Runnable cargarObras = new Runnable() {
            @Override
            public void run() {
                ObraDao dao = DBClient.getInstance(ObraListActivity.this).getObraDb().obraDao();
                listaObrasDataset =dao.getAll();
                runOnUiThread( hiloUpdateLista );
            }
        };
        Thread t1 = new Thread(cargarObras);
        t1.start();
    }

    class BorrarObraAsyncTask extends AsyncTask<Obra,Void,Void> {
        @Override
        protected Void doInBackground(Obra... obra) {
            ObraDao dao = DBClient.getInstance(ObraListActivity.this).getObraDb().obraDao();
            dao.delete(obra[0]);
            listaObrasDataset.clear();
            listaObrasDataset.addAll(dao.getAll());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter.notifyDataSetChanged();
            btnBorrarObra.setEnabled(false);
            btnEditarObra.setEnabled(false);
            lvObras.clearChoices();
            obraSeleccionada = null;
        }

    }

}
