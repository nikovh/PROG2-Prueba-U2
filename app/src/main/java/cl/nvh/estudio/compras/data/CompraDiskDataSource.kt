package cl.nvh.estudio.compras.data

import android.util.Log
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import cl.nvh.estudio.compras.data.modelo.Compra

class CompraDiskDataSource() {

    fun obtener(fileInputStream: FileInputStream):List<Compra> {
        return try {
            fileInputStream.use { fis ->
                ObjectInputStream(fis).use { ois ->
                    ois.readObject() as? List<Compra> ?: emptyList()
                }
            }
        } catch (fnfex: FileNotFoundException) {
            emptyList<Compra>()
        } catch (ex:Exception) {
            Log.e("CompraDiskDataSource", "obtener ex:Exception ${ex.toString()}")
            emptyList<Compra>()
        }
    }

    fun guardar(fileOutputStream: FileOutputStream, compras:List<Compra>) {
        fileOutputStream.use { fos ->
            ObjectOutputStream(fos).use { oos ->
                oos.writeObject(compras)
            }
        }
    }
}