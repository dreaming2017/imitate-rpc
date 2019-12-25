package com.hs.netty.imitate.rpc.serialize.support.hessian;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * Hessian序列化/反序列化对象工厂池
 * @author 王海生
 *
 */
public class HessianSerializeFactory extends BasePooledObjectFactory<HessianSerialize>{

	@Override
	public HessianSerialize create() throws Exception {
		return createHessian();
	}

	@Override
	public PooledObject<HessianSerialize> wrap(HessianSerialize hessian) {
		return new DefaultPooledObject<HessianSerialize>(hessian);
	}
	
	private HessianSerialize createHessian() {
        return new HessianSerialize();
    }

}