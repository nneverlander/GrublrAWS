/*
 * Copyright 2010-2013 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 * 
 *  http://aws.amazon.com/apache2.0
 * 
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.grublr.geo.dynamodb.internal;

import com.amazonaws.services.dynamodbv2.model.QueryRequest;

public class DynamoDBUtil {
	public static QueryRequest copyQueryRequest(QueryRequest queryRequest) {
		QueryRequest copiedQueryRequest = new QueryRequest().withAttributesToGet(queryRequest.getAttributesToGet())
				.withConsistentRead(queryRequest.getConsistentRead())
				.withExclusiveStartKey(queryRequest.getExclusiveStartKey()).withIndexName(queryRequest.getIndexName())
				.withKeyConditions(queryRequest.getKeyConditions()).withLimit(queryRequest.getLimit())
				.withReturnConsumedCapacity(queryRequest.getReturnConsumedCapacity())
				.withScanIndexForward(queryRequest.getScanIndexForward()).withSelect(queryRequest.getSelect())
				.withTableName(queryRequest.getTableName());

		return copiedQueryRequest;
	}
}
