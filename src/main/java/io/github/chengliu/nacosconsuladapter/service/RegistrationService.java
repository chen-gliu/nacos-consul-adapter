/**
 * The MIT License
 * Copyright © 2021 liu cheng
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.github.chengliu.nacosconsuladapter.service;


import io.github.chengliu.nacosconsuladapter.model.Result;
import io.github.chengliu.nacosconsuladapter.model.ServiceInstancesHealth;
import io.github.chengliu.nacosconsuladapter.model.ServiceInstancesHealthOld;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * Returns Services and List of Service with its last changed
 */
public interface RegistrationService {

    /**
     * get all service name
     * 获取所有服务名称
     *
     * @param waitMills
     * @param index
     * @return
     */
    Mono<Result<Map<String, List<Object>>>> getServiceNames(long waitMills, Long index);

    /**
     * get speci service all instance
     * 获取指定服务所有实例
     *
     * @param serviceName
     * @param waitMillis
     * @param index
     * @return
     */
    Mono<Result<List<ServiceInstancesHealth>>> getServiceInstancesHealth(String serviceName, long waitMillis, Long index);

    /**
     * 获取指定服务所有实例 老版
     * @param serviceName
     * @param waitMillis
     * @param index
     * @return
     */
    Mono<Result<List<ServiceInstancesHealthOld>>> getServiceInstancesHealthOld(String serviceName, long waitMillis, Long index);

}