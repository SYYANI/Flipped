/* Copyright 2017 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package cn.shu.heartsound.tflite;

import android.app.Activity;

import java.io.IOException;

/** This TensorFlow Lite classifier works with the quantized ClassifierTSCAN model. */
public class ClassifierTSCAN extends Classifier {
  /**
   * Initializes a {@code ClassifierMTTSCAN}.
   *
   * @param activity
   */
  public ClassifierTSCAN(Activity activity, Device device, int numThreads)
      throws IOException {
    super(activity, numThreads);
  }

  @Override
  protected String getModelPath() {
    return "model.tflite";
  }
}
