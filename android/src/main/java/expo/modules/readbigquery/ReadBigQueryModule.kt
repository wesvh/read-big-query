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
        AsyncFunction("readDictionary") { dataSerialized: String,  promise: Promise ->
            try {
                Log.d("ReadBigQueryModule", "Iniciando la conversión del diccionario")
                
                val gson = Gson()
                val type = object : TypeToken<List<List<Any>>>() {}.type
                val data: List<List<Any>> = gson.fromJson(dataSerialized, type)
                val dictionary = data[0] as List<String>
                val values = data.subList(1, data.size)

                Log.d("ReadBigQueryModule", "Diccionario: $dictionary")

                // ahora la idea es hacer un JSON con el diccionario y los valores, donde el diccionario sea el nombre de las llaves y los valores sean los valores de las llaves

                val result = mutableListOf<Map<String, Any>>()
                for (value in values) {
                    val map = mutableMapOf<String, Any>()
                    for (i in dictionary.indices) {
                        map[dictionary[i]] = value[i]
                    }
                    result.add(map)
                }

                // Supongamos que simplemente convertimos `data` de nuevo a un string JSON para el ejemplo.
                val resultString = gson.toJson(result)
                promise.resolve(resultString)
            } catch (e: Exception) {
                promise.reject("ERROR_READ_DICTIONARY", "Failed to read dictionary", e)
            }
        }
    }
}
