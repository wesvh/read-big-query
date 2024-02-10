package expo.modules.readbigquery

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import expo.modules.kotlin.Promise
import android.util.Log

class ReadBigQueryModule : Module() {
    override fun definition() = ModuleDefinition {
        Name("ReadBigQuery")
        AsyncFunction("readDictionary") { args: Array<Any>, promise: Promise ->
            try {
                Log.d("ReadBigQueryModule", "Iniciando la conversión del diccionario")
                val jsonString = args[0] as? String ?: throw IllegalArgumentException("Se esperaba un string JSON como argumento")
                
                val gson = Gson()
                val type = object : TypeToken<List<List<Any>>>() {}.type
                val data: List<List<Any>> = gson.fromJson(jsonString, type)

                // Aquí realizas las operaciones necesarias con `data`...

                // Supongamos que simplemente convertimos `data` de nuevo a un string JSON para el ejemplo.
                val resultString = gson.toJson(data)
                promise.resolve(resultString)
            } catch (e: Exception) {
                promise.reject("ERROR_READ_DICTIONARY", "Failed to read dictionary", e)
            }
        }
    }
}
