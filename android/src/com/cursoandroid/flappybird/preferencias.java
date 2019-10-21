package com.cursoandroid.flappybird;

import android.content.Context;
import android.content.SharedPreferences;

import org.w3c.dom.Text;

public class preferencias extends FlappyBird{

    private Context contexto;
    private SharedPreferences preferences;
    private String NOME_ARQUIVO = "recorde.preferencias";
    private int MODE = 0;
    private SharedPreferences.Editor editor;

    private String CHAVE_RECORDE = "recorde";
    private int recordePreferencia;
    FlappyBird flappyBird = new FlappyBird();

    @Override
    public void record(int record) {
        recordePreferencia = record;
        getSh
        super.record(record);
    }



    public void salvarRecordePreferencia (String recorde){
        editor.putString(CHAVE_RECORDE, recorde);
        editor.commit();
    }

    public String getRedorde(){
        String recorde =preferences.getString(CHAVE_RECORDE, null);
        return recorde;
    }


}
