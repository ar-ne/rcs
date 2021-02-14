package ar.ne.rcs.shared.models.common;

import ar.ne.rcs.shared.interfaces.Json;
import com.google.gson.Gson;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class JsonModel<T> implements Json {
    private String ref;
    private String dat;
    private T obj;

    @SuppressWarnings("unchecked")
    public JsonModel(String json) throws ClassNotFoundException {
        JsonModel<?> self = new Gson().fromJson(json, JsonModel.class);
        this.ref = self.ref;
        this.dat = self.dat;
        this.obj = (T) new Gson().fromJson(dat, Class.forName(ref));

    }

    public JsonModel(T obj) {
        this.ref = obj.getClass().getName();
        this.dat = new Gson().toJson(obj);
        this.obj = obj;
    }

    public T get() {
        return obj;
    }

    @Override
    public String toJSON() {
        dat = new Gson().toJson(this);
        return dat;
    }

    @Override
    public String toString() {
        return toJSON();
    }
}
