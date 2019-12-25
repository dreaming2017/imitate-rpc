package com.hs.netty.imitate.rpc.serialize.support.hessian;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.hs.netty.imitate.rpc.serialize.support.RpcSerialize;

public class HessianSerialize implements RpcSerialize {

	@Override
	public void serialize(OutputStream output, Object object)
			throws IOException {
		Hessian2Output ho = null;
		try {
			ho = new Hessian2Output(output);
			ho.startMessage();
			ho.writeObject(object);
			ho.completeMessage();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ho != null) {
				ho.close();
			}
			output.close();
		}
	}

	@Override
	public Object deserialize(InputStream input) throws IOException {
		Object result = null;
		Hessian2Input hi = null;
		try {
			hi = new Hessian2Input(input);
			hi.startMessage();
			result = hi.readObject();
			hi.completeMessage();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (hi != null) {
				hi.close();
			}
		}
		return result;
	}

}
