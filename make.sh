#!/bin/sh

###############################################################################
#
#  autumo ifaceX Office Reader/Writer packager.
#  Version: 1.1
#
#  Notes:
#   -
#
#------------------------------------------------------------------------------
#
#  2024 autumo GmbH
#  Date: 29.07.2024
#
###############################################################################




# VARS
IPRW_OFFICE_VERSION=2.1.0




# ------------------------------------------------
# -------- Usage
# ------------------------------------------------
if [ "$1" = "help" -o "$#" -lt 1 ]
then
	echo " "
	echo "make clear"
	echo "make create"
	echo " "
	echo " "
	exit 1
fi


mkdir -p product


# ------------------------------------------------
# -------- DELETE PRODUCT
# ------------------------------------------------
if [ "$1" = "clear" ]
then
	cd product

	# remove working directory
	if [ -d "autumo-ifaceX-iprw-office-$IPRW_OFFICE_VERSION" ]
	then
		rm -Rf autumo-ifaceX-iprw-office-$IPRW_OFFICE_VERSION
	fi
	
	# remove package
	if [ -f "autumo-ifaceX-iprw-office-$IPRW_OFFICE_VERSION.zip" ]
	then
    	rm autumo-ifaceX-iprw-office-*.zip
	fi
	
	cd ..
	
	exit 1
fi



# ------------------------------------------------
# -------- CREATE PRODUCT
# ------------------------------------------------
if [ "$1" = "create" -a "$#" -gt 0 ]
then



# -----------------------------
# ---- Cleanup & Prepare
# -----------------------------

	echo "-> Cleanup & prepare..."
	
	# delete old product package
	if [ -d "product/autumo-ifaceX-iprw-office-$IPRW_OFFICE_VERSION" ]
	then
		rm -Rf product/autumo-ifaceX-iprw-office-$IPRW_OFFICE_VERSION
	fi	

	# go to product
	cd product
	
	# make working directory
	mkdir autumo-ifaceX-iprw-office-$IPRW_OFFICE_VERSION



# -----------------------------
# ---- Go to ifaceX package
# -----------------------------

	# go to ifaceX package folder
	cd autumo-ifaceX-iprw-office-$IPRW_OFFICE_VERSION



# -----------------------------
# ---- Copying
# -----------------------------
		

	echo "-> Copying..."
	
	mkdir -p lib


	##  Sorting out libs beforehand, example:
	#LIST=(`ls ../../mod-google-storage/lib/*.jar`)
	#LIST=${LIST[@]/*failureaccess*}
	#LIST=${LIST[@]/*google-oauth-client*}
	#LIST=${LIST[@]/*j2objc-annotations*}
	#LIST=${LIST[@]/*jsr305*}
	#LIST=${LIST[@]/*listenablefuture*}
	#cp $LIST lib/


	# COPY LIBS
	cp ../../lib/*.jar lib/


	# REMOVE ifaceX Doublettes
	rm lib/commons-codec*
	rm lib/commons-io*
	rm lib/slf4j-api*
	rm lib/log4j-api*
	
	
	# COPY Templates
	
	mkdir -p interfaces/examples
	cp ../../interfaces/*.* interfaces/
	cp ../../interfaces/examples/*.* interfaces/examples/

	
	# COPY LICENSE
	cp ../../LICENSE.md .
	# COPY README
	cp ../../README.md .

	

# -----------------------------
# ---- Create Product
# -----------------------------

	echo "-> Create PRODUCT..."

	# LEAVE PACKAGE FOLDER
	cd ..

	# create archive
	zip -r "autumo-ifaceX-iprw-office-${IPRW_OFFICE_VERSION}.zip" autumo-ifaceX-iprw-office-${IPRW_OFFICE_VERSION} \
		-x "*/.DS_Store" \
		-x "*/__MACOSX"

	# delete build directory
	rm -Rf autumo-ifaceX-iprw-office-${IPRW_OFFICE_VERSION}
	

	
# -----------------------------
# ---- END
# -----------------------------
	
	# leave product folder
	cd ..

	
else
	echo "Nope! -> make create"
	echo " "
fi



