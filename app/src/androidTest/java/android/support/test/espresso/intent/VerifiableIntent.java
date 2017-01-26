/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.support.test.espresso.intent;

/**
 * A {@link ResolvedIntent} that can be marked as verified.
 */
public interface VerifiableIntent extends ResolvedIntent {
  /**
   * Returns {@code true} if this recorded intent has been verified.
   */
  boolean hasBeenVerified();

  /**
   * Marks this recorded intent as verified.
   */
  void markAsVerified();
}
