package com.firecrow.windmill.model

import android.os.Bundle

class ExtNValue(
    val _id: Long = -1,
    val type: String?,
    val int_valaue: Int?,
    val float_valaue: Float?,
    val string_valaue: String?,
    val boolean_valaue: Boolean?,
) {
    fun getValueString(): String?{
        if(type == "Int") return this.int_valaue.toString();
        if(type == "Float") return this.float_valaue.toString();
        if(type == "Boolean") return this.boolean_valaue.toString();
        if(type == "String") return this.string_valaue;
        return null
    }
    override fun toString(): String {
        return "Value<${this.type}: ${this.getValueString()}>"
    }
}

class ExtNValuesMap {
    companion object {
        fun FromBundle(bundle:Bundle): MutableMap<String, ExtNValue>{
            val results = mutableMapOf<String, ExtNValue>()
            for(key in bundle.keySet()) {
                val value = bundle.get(key)
                var extnvalue = ExtNValue(-1, "String",null, null, value.toString(), null)
                if(value is Int || value is Long){
                    extnvalue = ExtNValue(-1, "Int",value as Int, null, null, null)
                }else if(value is Boolean){
                    extnvalue = ExtNValue(-1, "Boolean", null, null, null, value as Boolean)
                }else if(value is Float){
                    extnvalue = ExtNValue(-1, "Float", null, value as Float, null, null)
                }
                results[key] = extnvalue
            }
            return results
        }
    }
}


// values persistor
