import random
from datetime import datetime, timedelta

# Configuration
NUM_FILES = 3
DAYS = 90  # approx 3 months
END_TIME_STR = "2025-06-15T09:43:34.484Z"

# Starting initial values for each machine: (cpu_threads, storage_kb, ram_gb)
INITIALS = [
    (10, 200 * 1024**3, 8),        # Machine 1: 10 threads, 200 GB storage, 8 GB RAM
    (100, 100000000000, 100),      # Machine 2: 100 threads, 100e9 KB storage, 100 GB RAM
    (100, 100000000000, 100)       # Machine 3: same as above
]

# Probability of fluctuation per day (~2 times/week -> approx 2/7)
FLUCT_PROB = 2/7

# Fluctuation ranges:
CPU_FLUCT_RANGE = (4, 12)          # +/- threads
STORAGE_FLUCT_GB = (10, 100)      # GB to KB
RAM_FLUCT_GB = (1, 10)            # GB fluctuation (around 10 GB)

# Parse end time and compute start
from datetime import datetime, timedelta
end_time = datetime.strptime(END_TIME_STR, "%Y-%m-%dT%H:%M:%S.%fZ")
start_time = end_time - timedelta(days=DAYS)

def format_ts(dt):
    return dt.strftime("%Y-%m-%dT%H:%M:%S.%f")[:-3] + "Z"

# Generate files
timestamps = [start_time + timedelta(days=i) for i in range(DAYS + 1)]

for idx, (init_cpu, init_storage, init_ram) in enumerate(INITIALS, start=1):
    cpu = init_cpu
    storage = init_storage
    ram = init_ram
    with open(f"{idx}.csv", "w") as f:
        for ts in timestamps:
            # Possibly fluctuate
            import random
            if random.random() < FLUCT_PROB:
                # CPU threads
                delta_cpu = random.randint(*CPU_FLUCT_RANGE)
                cpu = max(0, cpu + random.choice([-1, 1]) * delta_cpu)
                # Storage in KB
                delta_storage = random.randint(*STORAGE_FLUCT_GB) * 1024**3
                storage = max(0, storage + random.choice([-1, 1]) * delta_storage)
                # RAM in GB
                delta_ram = random.randint(*RAM_FLUCT_GB)
                ram = max(0, ram + random.choice([-1, 1]) * delta_ram)
            # Write record
            f.write(f"{format_ts(ts)},{cpu},{storage},{ram}\n")

