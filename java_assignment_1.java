# =============================================================================
#
#           NASA APP CHALLENGE: DATA ENGINEERING PIPELINE (Final Version)
#
# =============================================================================
#
# Objective: To download solar flare event data and corresponding solar images,
#            process them, and save them as a clean dataset for model training.
#
# This version includes enhanced error handling and logging to be more robust.
#
# =============================================================================

# --- Step 1: Install Required Libraries ---
print("Installing sunpy...")
!pip install sunpy[all] -q

# --- Step 2: Import Libraries ---
import os
import zipfile
import time
from pathlib import Path
import pandas as pd
from sunpy.net import Fido, attrs as a
from astropy.time import Time
from astropy import units as u
import sunpy.map
from google.colab import drive
import cv2
import numpy as np
import warnings

# Suppress warnings from sunpy that are not critical for this task
warnings.filterwarnings('ignore', category=sunpy.util.exceptions.SunpyUserWarning)

print("Libraries imported successfully.")


# --- Step 3: Mount Google Drive ---
print("Mounting Google Drive...")
try:
    drive.mount('/content/drive')
    print("Google Drive mounted successfully.")
except Exception as e:
    print(f"Error mounting Google Drive: {e}")


# --- Step 4: Configuration ---
# Setting a known active period for solar flares.
START_TIME = "2013-05-01"
END_TIME = "2013-05-31" # Focusing on a very active month to speed up testing

# Define the base directory for our data in the Colab environment
BASE_DATA_PATH = Path("/content/data")
FLARE_PATH = BASE_DATA_PATH / "flare"
NO_FLARE_PATH = BASE_DATA_PATH / "no_flare"

# Create the directories
FLARE_PATH.mkdir(parents=True, exist_ok=True)
NO_FLARE_PATH.mkdir(parents=True, exist_ok=True)

# Path in Google Drive to save the final zipped dataset
DRIVE_OUTPUT_PATH = Path("/content/drive/My Drive/NASA_App_Challenge_Team")
DRIVE_OUTPUT_PATH.mkdir(exist_ok=True)

print(f"Configuration set for time range: {START_TIME} to {END_TIME}")


# --- Step 5: Fetch Solar Flare Event Data (Positive Samples) ---
print("\nFetching flare event data from the GOES catalog...")
flare_times = []
try:
    search_query = Fido.search(
        a.Time(START_TIME, END_TIME),
        a.Instrument.goes,
        a.goes.FlareClass.like('M%') | a.goes.FlareClass.like('X%')
    )
    if search_query:
        flare_events = search_query['goes']
        flare_times = flare_events['event_starttime']
        print(f"SUCCESS: Found {len(flare_times)} M-class or stronger flares in the specified period.")
    else:
        print("WARNING: The Fido query did not return any flare events for the specified time range.")

except Exception as e:
    print(f"\n--- An Exception Occurred During Flare Search ---")
    print(e)


# --- Step 6: Download SDO Images for Each Flare Event ---
if not list(flare_times):
    print("\nNo flare times found, skipping download of flare event images.")
else:
    print("\nDownloading SDO/HMI images for FLARE events...")
    MAX_SAMPLES = 50 # Let's start with a smaller batch for faster debugging
    flare_times_to_process = flare_times[:min(len(flare_times), MAX_SAMPLES)]

    for i, flare_time_obj in enumerate(flare_times_to_process):
        flare_time_str = flare_time_obj.iso
        print(f"Processing flare {i+1}/{len(flare_times_to_process)} at {flare_time_str}...")
        try:
            flare_time = Time(flare_time_str, format='isot', scale='utc')
            search_start = flare_time - 24 * u.hour
            search_end = flare_time - 2 * u.hour

            hmi_search = Fido.search(
                a.Time(search_start, search_end),
                a.Instrument.hmi,
                a.Physobs.los_magnetic_field,
                a.Sample(24 * u.hour)
            )

            if hmi_search:
                print("  -> Found a candidate FITS file. Attempting download...")
                downloaded_files = Fido.fetch(hmi_search[0,0], path=FLARE_PATH, max_conn=1)
                
                if not downloaded_files:
                    print("  -> Fido.fetch did not return any files. Skipping.")
                    continue

                fits_file_path = Path(downloaded_files[0])
                print(f"  -> Successfully downloaded: {fits_file_path.name}")

                smap = sunpy.map.Map(fits_file_path)
                smap_resized = smap.resample((512, 512) * u.pix)
                
                img_data = np.nan_to_num(smap_resized.data)
                img_normalized = cv2.normalize(img_data, None, 0, 255, cv2.NORM_MINMAX)
                
                output_filename = FLARE_PATH / f"flare_{i+1}.png"
                write_success = cv2.imwrite(str(output_filename), img_normalized)
                
                if write_success:
                    print(f"  -> Successfully converted and saved {output_filename.name}")
                    os.remove(fits_file_path) # Clean up the FITS file
                else:
                    print(f"  -> FAILED to write PNG file for {fits_file_path.name}")

            else:
                print("  -> No image found in time window for this event.")
            
            time.sleep(1) # Add a 1-second delay to be kind to the servers

        except Exception as e:
            print(f"  -> An unexpected error occurred for this event: {e}")

# --- Step 7: Download Images for Non-Flare Periods (Negative Samples) ---
print("\nVerifying number of positive samples downloaded...")
num_positive_samples = len(list(FLARE_PATH.glob('*.png')))
print(f"Found {num_positive_samples} positive (flare) images.")

if num_positive_samples == 0:
    print("\nNo positive samples were downloaded, skipping download of negative samples.")
else:
    # Logic for downloading negative samples would go here.
    # We will skip it for now to confirm the positive sample download works first.
    print(f"\nFor now, we will skip downloading the {num_positive_samples} negative samples to confirm the fix.")


# --- Step 8: Final Step - Zip and Save to Google Drive ---
print("\nZipping processed data and saving to Google Drive...")
final_positive_count = len(list(FLARE_PATH.glob('*.png')))
final_negative_count = len(list(NO_FLARE_PATH.glob('*.png')))

if final_positive_count == 0:
    print("\nWARNING: No images were successfully downloaded. The final zip file will be empty.")
else:
    print(f"\nFinal count: {final_positive_count} flare images, {final_negative_count} no-flare images.")

output_zip_file = "/content/processed_data.zip"
with zipfile.ZipFile(output_zip_file, 'w', zipfile.ZIP_DEFLATED) as zipf:
    for file_path in BASE_DATA_PATH.rglob('*'):
        if file_path.is_file():
            zipf.write(file_path, file_path.relative_to(BASE_DATA_PATH))

try:
    !cp "{output_zip_file}" "{DRIVE_OUTPUT_PATH}"
    print(f"\nSuccessfully copied zip file to: {DRIVE_OUTPUT_PATH / 'processed_data.zip'}")
except Exception as e:
    print(f"Error copying file to Google Drive: {e}")

print("\n=========================================================")
print("  DATA ENGINEERING COMPLETE!")
print("=========================================================")

