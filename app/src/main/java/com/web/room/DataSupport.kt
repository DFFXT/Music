package org.litepal.crud

import org.litepal.FluentQuery
import org.litepal.Operator
import org.litepal.crud.async.SaveExecutor
import org.litepal.crud.async.UpdateOrDeleteExecutor

open class DataSupport : LitePalSupport() {
    companion object {
        @JvmStatic
        fun where(vararg args: String?): FluentQuery {
            return Operator.where(*args)
        }

        @JvmStatic
        fun <T> findAll(clazz: Class<*>, vararg id: Long): MutableList<T> {
            return Operator.findAll(clazz, *id) as MutableList<T>
        }

        @JvmStatic
        fun <T> findAll(clazz: Class<*>, isEager: Boolean, vararg id: Long): MutableList<T> {
            return Operator.findAll(clazz, isEager, *id) as MutableList<T>
        }

        @JvmStatic
        fun <T> findFirst(clazz: Class<*>, isEager: Boolean): T? {
            return Operator.findFirst(clazz, isEager) as? T
        }

        @JvmStatic
        fun count(clazz: Class<*>): Int {
            return Operator.count(clazz)
        }

        @JvmStatic
        fun deleteAll(clazz: Class<*>) {
            Operator.deleteAll(clazz)
        }

        @JvmStatic
        fun deleteAll(clazz: Class<*>, condition: String, vararg args: String) {
            Operator.deleteAll(clazz, condition, *args)
        }

        @JvmStatic
        fun deleteAllAsync(clazz: Class<*>): UpdateOrDeleteExecutor {
            return Operator.deleteAllAsync(clazz)
        }

        fun <T : LitePalSupport> saveAllAsync(collection: Collection<T>): SaveExecutor {
            return Operator.saveAllAsync(collection)
        }
    }
}
