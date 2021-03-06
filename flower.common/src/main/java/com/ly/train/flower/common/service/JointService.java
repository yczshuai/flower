package com.ly.train.flower.common.service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.ly.train.flower.common.service.message.FlowMessage;

public class JointService implements Service, Joint {

  int sourceNumber = 0;

  // <messageId,Set<message>>
  Map<String, Set<Object>> resultMap = new ConcurrentHashMap<String, Set<Object>>();
  // <messageId,sourceNumber>
  Map<String, Integer> resultNumberMap = new ConcurrentHashMap<String, Integer>();

  @Override
  public Object process(Object message) {

    FlowMessage flowMessage = (FlowMessage) message;

    // first joint message
    if (!resultMap.containsKey(flowMessage.getTransactionId())) {
      Set<Object> objectSet = new HashSet<Object>();
      resultMap.put(flowMessage.getTransactionId(), objectSet);
      resultNumberMap.put(flowMessage.getTransactionId(), sourceNumber);
    }
    resultMap.get(flowMessage.getTransactionId()).add(((FlowMessage) message).getMessage());

    Integer integer = resultNumberMap.get(flowMessage.getTransactionId()) - 1;
    resultNumberMap.put(flowMessage.getTransactionId(), integer);
    if (integer <= 0) {
      Set<Object> returnObject = resultMap.get(flowMessage.getTransactionId());
      resultMap.remove(flowMessage.getTransactionId());
      resultNumberMap.remove(flowMessage.getTransactionId());

      return returnObject;
    }
    // TODO resultNumberMap&resultMap memory leak
    return null;
  }

  @Override
  // sourceNumber++ when initialize
  public void setSourceNumber(int number) {
    sourceNumber = number;
  }

}
