package com.tereigo.atlas_expr.unused;

public final class AstResultImpl<OUTPUT_TYPE> implements AstResult<OUTPUT_TYPE> {
    private OUTPUT_TYPE result;

    @Override
    public void set(OUTPUT_TYPE output) {
        this.result = output;
    }

    public OUTPUT_TYPE get() {
        return result;
    }

//    public void reset() {
//        this.result = null;
//    }

//    public boolean isValid() {
//        return result != null;
//    }

}
