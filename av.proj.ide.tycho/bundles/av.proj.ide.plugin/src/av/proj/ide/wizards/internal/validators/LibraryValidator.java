/*
 * This file is protected by Copyright. Please refer to the COPYRIGHT file
 * distributed with this source distribution.
 *
 * This file is part of OpenCPI <http://www.opencpi.org>
 *
 * OpenCPI is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * OpenCPI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package av.proj.ide.wizards.internal.validators;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

import av.proj.ide.wizards.internal.NewOcpiAssetWizardPage1;

public class LibraryValidator implements IValidator {
	
	private NewOcpiAssetWizardPage1 page;
	
	public LibraryValidator(NewOcpiAssetWizardPage1 page) {
		super();
		this.page = page;
	}
	
	@Override
	public IStatus validate(Object arg0) {
		String value = arg0.toString();
		if (value.length() == 0) {
			String err = "Library name must be specified";
			this.page.updateStatus(err);
			return ValidationStatus.error(err);
		}
		this.page.updateStatus(null);
		return ValidationStatus.ok();
	}

}
