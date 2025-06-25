import csv
import random
from datetime import datetime, timedelta

# Configuration
NUM_FILES = 3
days = 90  # approx 3 months
end_time_str = "2025-06-15T08:45:04.487Z"
threshold = 50 * 3600  # hours in seconds

# Parse end time
end_time = datetime.strptime(end_time_str, "%Y-%m-%dT%H:%M:%S.%fZ")
start_time = end_time - timedelta(days=days)

def format_ts(dt):
    # ISO with milliseconds and Z
    return dt.strftime("%Y-%m-%dT%H:%M:%S.%f")[:-3] + "Z"

for file_idx in range(1, NUM_FILES + 1):

    i = random.uniform(0, 100)

    # Initialize counters
    runtime = random.uniform(0, threshold)
    memory = random.uniform(400_000, 3000_000)  # initial memory in KB

    with open(f"{file_idx}.csv", "w", newline='') as f:
        writer = csv.writer(f)
        current_time = start_time
        j = 0
        for _ in range(days + 1):

            # Simulate daily increment
            # Server runtime: if running, add hours randomly
            j += 1
            inc = random.uniform(0, 3600 * 10)
            if j == i:
                inc -= threshold

            runtime += inc

            # When exceeding threshold, subtract enough to indicate extension (go negative)
            if runtime > threshold:

                runtime = threshold
                # Ceil multiple
                if random.random() < 0.2:
                    m = (runtime + threshold - 1) // threshold
                    runtime = runtime - m * threshold

            # Memory always grows by 0-90 MB per day
            memory += random.uniform(0, 90_000)

            # Write the record
            writer.writerow([
                format_ts(current_time),
                int(runtime),
                int(memory)
            ])

            # Next day (same time)
            current_time += timedelta(days=1)

